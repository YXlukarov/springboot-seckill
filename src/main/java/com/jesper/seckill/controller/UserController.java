package com.jesper.seckill.controller;

import com.jesper.seckill.bean.User;
import com.jesper.seckill.redis.RedisService;
import com.jesper.seckill.result.Result;
import com.jesper.seckill.service.UserService;
import com.jesper.seckill.vo.InfoVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping("/info/updateInfo")
    @ResponseBody
    public Result<String> updateInfo(String token, User user, @Valid InfoVo infoVo) {
        log.info(infoVo.toString());
        redisService.get();
        String token = userService.updateInfo(, user.getId(), infoVo);
    }
}