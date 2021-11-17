package com.jesper.seckill.controller;

import com.jesper.seckill.result.CodeMsg;
import com.jesper.seckill.result.Result;
import com.jesper.seckill.service.UserService;
import com.jesper.seckill.vo.LoginVo;
import com.jesper.seckill.vo.RegVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

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
        return "scdkill_login";
    }
    @RequestMapping("/scdkill_index")
    public String toindex() {
        return "scdkill_index";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> doLogin(HttpServletResponse response, @Valid LoginVo loginVo) {//加入JSR303参数校验
        log.info(loginVo.toString());
        System.out.println(loginVo.getMobile());
        String token = userService.login(response, loginVo);
        return Result.success(token);
    }


    @RequestMapping("/home")
    public String homePage(){
        return "newindex";
    }

    @RequestMapping("/newshop")
    public String newshopPage(){
        return "newshop";
    }


    @RequestMapping("/to_register")
    public String toRegister() { return "register"; }



    @RequestMapping("/cart")
    public String toscdkillCart(){
        return "scdkill_cart";
    }

    @RequestMapping("/info")
    public String toscdkillInfo(){
        return "scdkill_info";
    }

    @RequestMapping("/productdetail")
    public String toscdkilldetail(){
        return "scdkill_goodsdetails";
    }

    @RequestMapping("/pay")
    public String toscdkillpay(){
        return "scdkill_pay";
    }




    @RequestMapping("do_register")
    @ResponseBody
    public Result<String> doRegister(HttpServletResponse response, @Valid RegVo regVo) {
        log.info("call do_register");
        log.info(regVo.toString());
        boolean reg = userService.register(response, regVo);
        if (reg) {
            return Result.success("Register success!");
        }
        else return Result.error(CodeMsg.SERVER_ERROR);
    }

}
