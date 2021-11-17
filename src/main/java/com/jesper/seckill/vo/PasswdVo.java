package com.jesper.seckill.vo;

import com.jesper.seckill.validator.IsPassword;

import javax.validation.constraints.NotNull;

public class PasswdVo {

    @NotNull
    @IsPassword
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "InfoVo{" +
                "password='" + password +
                '}';
    }
}
