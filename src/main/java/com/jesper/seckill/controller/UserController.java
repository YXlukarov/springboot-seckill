package com.jesper.seckill.controller;

import com.jesper.seckill.bean.User;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.result.Result;
import com.jesper.seckill.service.UserService;
import com.jesper.seckill.vo.InfoVo;
import com.jesper.seckill.vo.PasswdVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * Created by jiangyunxiong on 2018/5/23.
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    RedisService redisService;

    @RequestMapping("/info")
    @ResponseBody
    public Result<User> info(Model model, User user) {
        return Result.success(user);
    }
    @RequestMapping("/info/to_update")
    public String toInfo() {
        return "user_info";
    }
    @RequestMapping("/info/updateInfo")
    @ResponseBody
    public Result<String> updateInfo(HttpServletRequest request, HttpServletResponse response, User user, @Valid InfoVo infoVo) {
        log.info(infoVo.toString());
        System.out.println(infoVo);
        //System.out.println(user.toString());
        boolean ui = userService.updateInfo(request, response, user, infoVo);
        if (ui) {
            System.out.println("update");
            return Result.success("更新信息成功");
        }
        else return Result.error(CodeMsg.UPDATE_ERROR);
    }
    @RequestMapping("/info/updatePasswd")
    @ResponseBody
    public Result<String> updatePasswd(HttpServletRequest request, HttpServletResponse response, User user, @Valid PasswdVo passwdVo) {
        log.info(passwdVo.toString());
        boolean up = userService.updatePassword(request, response, user, passwdVo.getPassword());
        if (up) {
            System.out.println("Password changed");
            return Result.success("修改密码成功");
        }
        else return Result.error(CodeMsg.UPDATE_ERROR);
    }
}