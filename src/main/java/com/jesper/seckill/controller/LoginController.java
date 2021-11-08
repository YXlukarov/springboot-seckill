package com.jesper.seckill.controller;

import com.jesper.seckill.bean.User;
import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.result.Result;
import com.jesper.seckill.service.UserService;
import com.jesper.seckill.vo.LoginVo;
import com.jesper.seckill.vo.RegVo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


/**
 * Created by jiangyunxiong on 2018/5/21.
 */
@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {

    //private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletRequest request, HttpServletResponse response, @Valid LoginVo loginVo) {//加入JSR303参数校验
        log.info(loginVo.toString());
        String token = userService.login(request, response, loginVo);
        return Result.success(token);
    }

    @RequestMapping("/check_login")
    @ResponseBody
    public Result<String> checkLogin(HttpServletRequest request, HttpServletResponse response, User user) {
        if (user == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        String token = userService.beforeLogin(request, response);
        if (token == null)
            return Result.error(CodeMsg.SESSION_ERROR);
        else return Result.success("1");
    }

    @RequestMapping("/to_register")
    public String toRegister() { return "register"; }

    @RequestMapping("do_register")
    @ResponseBody
    public Result<String> doRegister(HttpServletResponse response, @Valid RegVo regVo) {
        log.info(regVo.toString());
        boolean reg = userService.register(response, regVo);
        if (reg) {
            return Result.success("Register success!");
        }
        else return Result.error(CodeMsg.SERVER_ERROR);
    }

}
