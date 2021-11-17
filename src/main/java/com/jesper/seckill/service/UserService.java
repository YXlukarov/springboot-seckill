package com.jesper.seckill.service;

import com.alibaba.druid.util.StringUtils;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.exception.GlobalException;
import com.jesper.seckill.mapper.UserMapper;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.redis.UserKey;
import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.result.Result;
import com.jesper.seckill.util.DBUtil;
import com.jesper.seckill.util.MD5Util;
import com.jesper.seckill.util.UUIDUtil;
import com.jesper.seckill.vo.InfoVo;
import com.jesper.seckill.vo.LoginVo;
import com.jesper.seckill.vo.RegVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jiangyunxiong on 2018/5/22.
 */
@Service
@Slf4j
public class UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisService redisService;

    public static final String COOKIE_NAME_TOKEN = "token";

    public User getById(long id) {
        //对象缓存
        User user = redisService.get(UserKey.getById, "" + id, User.class);
        if (user != null) {
            return user;
        }
        //取数据库
        user = userMapper.getById(id);
        //再存入缓存
        if (user != null) {
            redisService.set(UserKey.getById, "" + id, user);
        }
        return user;
    }

    /**
     * 典型缓存同步场景：更新密码
     */
    public boolean updatePassword(HttpServletRequest request, HttpServletResponse response, User user, String formPass) {
        String token = getTokenFromRequest(request);
        if (StringUtils.isEmpty(token)){
            log.warn("未获取到token");
            User u = userMapper.getById(user.getId());
            if (u == null){
                log.warn("用户不存在");
                throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
            }
            else {
                log.info("用户信息存在于数据库，为用户分配新token");
                token = UUIDUtil.uuid(); // 为用户重新分配token
                log.info("为"+user+"分配新的token"+token);
                addCookie(response, token, user);
            }
        }
        //更新数据库
        User toBeUpdate = new User();
        toBeUpdate.setId(user.getId());
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        userMapper.updatePassword(toBeUpdate);
        //更新缓存：先删除再插入
        redisService.delete(UserKey.token, token);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token, token, user);
        return true;
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String paramToken = request.getParameter(COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, COOKIE_NAME_TOKEN);
        System.out.println(paramToken);
        System.out.println(cookieToken);
        return StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
    }

    public boolean updateInfo(HttpServletRequest request, HttpServletResponse response, User user, InfoVo infoVo) {
        String token = getTokenFromRequest(request);
        if (StringUtils.isEmpty(token)) {
            log.warn("未获取到token");
            User u = userMapper.getById(user.getId());
            log.info("尝试从数据库中读取用户信息");
            if (u != null) {
                token = UUIDUtil.uuid(); // 为用户重新分配token
                log.info("为"+user+"分配新的token"+token);
                addCookie(response, token, user);
            }
            else{
                log.warn("用户不存在!");
                throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
            }
        }
        // 下列操作的前提是token一定存在
        // 更新数据库
        user.setNickname(infoVo.getNickname());
        user.setHead(infoVo.getHead());
        userMapper.updateInfo(user);
        // 更新缓存
        redisService.delete(UserKey.token, token); //先删除user原先的token
        addCookie(response, token, user); //插入新token
        return true;

    }

    public String beforeLogin(HttpServletRequest request, HttpServletResponse response){
        // 最开始确认request中是否有token
        String paramToken = request.getParameter(COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, COOKIE_NAME_TOKEN);
        System.out.println(paramToken);
        System.out.println(cookieToken);
        // 若token存在
        if (StringUtils.isEmpty(cookieToken) || StringUtils.isEmpty(paramToken)) {
            // 获取存在的token
            String token = StringUtils.isEmpty(paramToken) ? cookieToken : paramToken;
            // 根据token获取用户，具体见getByToken函数
            User tokenUser = getByToken(response, token);
            if (tokenUser != null) {
                log.info("已根据token从redis中获取到用户"+tokenUser+",可跳过验证密码操作");
                //System.out.println("已根据token从redis中获取到用户"+tokenUser);
                return token;
            }
        }
        return null;
    }

    public String login(HttpServletRequest request, HttpServletResponse response, LoginVo loginVo) {
        String t = beforeLogin(request, response);
        if (t != null) {
            log.info("已获取token，跳过密码验证");
            return t;
        }
        log.info("开始验证密码操作");
        // request中没有token，进行login操作
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword(); // 用户输入的密码经过第一次md5加密后所得
        //判断手机号是否存在
        User user = userMapper.getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB); // 第二次md5加密
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        user.setLoginCount(user.getLoginCount()+1);
        user.setLastLoginDate(new Date());
        // 更新用户的最后登录日期和登录次数
        userMapper.updateLogin(user);
        //生成唯一id作为token
        String token = UUIDUtil.uuid();
        addCookie(response, token, user); // 见下
        return token;
    }

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    public void addCookie(HttpServletResponse response, String token, User user) {
        redisService.set(UserKey.token, token, user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(UserKey.token.expireSeconds());
        cookie.setPath("/");//设置为网站根目录
        response.addCookie(cookie);
    }
    // 从request中获取指定值
    private String getCookieValue(HttpServletRequest request, String cookiName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookiName)) {
                return cookie.getValue();
            }
        }
        return null;
    }
    // 从response中拿取token
    public String getTokenFromResponse(HttpServletResponse response) {
        String cookie = response.getHeader("set-cookie");
        if (cookie == null)
            return "none";
        String[] strings = cookie.split(";");
        String token = strings[0].split(":")[1]; //直接取到token
        System.out.println(token);
        return token;
    }

    /**
     * 根据token获取用户信息
     */
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserKey.token, token, User.class);
        //延长有效期，有效期等于最后一次操作+有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    public boolean register(HttpServletResponse response, RegVo regVo){
        if (regVo == null){
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        log.info("call userservice");
        String mobile = regVo.getMobile();
        String passwd = regVo.getPassword();
        String nickname = regVo.getNickname();
        User user = getById(Long.parseLong(mobile));
        if (user != null) {
            throw new GlobalException(CodeMsg.USER_EXIST);
        }
        User u = new User();
        u.setId(Long.parseLong(mobile));
        u.setLoginCount(1);
        u.setNickname(nickname);
        u.setRegisterDate(new Date());
        u.setSalt("1a2b3c4d");
        u.setPassword(MD5Util.inputPassToDbPass(passwd, u.getSalt()));
        log.info(u.toString());
        return userMapper.register(u);
    }
}
