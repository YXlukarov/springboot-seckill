package com.jesper.seckill.service;

import com.alibaba.druid.util.StringUtils;
import com.jesper.seckill.bean.User;
import com.jesper.seckill.exception.GlobalException;
import com.jesper.seckill.mapper.UserMapper;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.redis.UserKey;
import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.util.DBUtil;
import com.jesper.seckill.util.MD5Util;
import com.jesper.seckill.util.UUIDUtil;
import com.jesper.seckill.vo.LoginVo;
import com.jesper.seckill.vo.RegVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
    public boolean updatePassword(String token, long id, String formPass) {
        //取user
        User user = getById(id);
        if(user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //更新数据库
        User toBeUpdate = new User();
        toBeUpdate.setId(id);
        toBeUpdate.setPassword(MD5Util.formPassToDBPass(formPass, user.getSalt()));
        userMapper.update(toBeUpdate);
        //更新缓存：先删除再插入
        redisService.delete(UserKey.getById, ""+id);
        user.setPassword(toBeUpdate.getPassword());
        redisService.set(UserKey.token, token, user);
        return true;
    }

    public String login(HttpServletResponse response, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword(); // 用户输入的密码
        //判断手机号是否存在
        User user = getById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassToDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
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
