package com.jesper.seckill.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiangyunxiong on 2018/5/22.
 *
 * 登录校验工具类
 */
public class ValidatorUtil {

    //默认以1开头后面加10个数字为手机号
    private static final Pattern mobile_pattern = Pattern.compile("1\\d{10}");
    /*
        当前真实使用的手机号码的验证正则式
        private static final String regex = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1、5、8、9]))\\d{8}$";
        Pattern mobile_pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
    */

    public static boolean isMobile(String src){
        if(StringUtils.isEmpty(src)){
            return false;
        }
        Matcher m = mobile_pattern.matcher(src);
        return m.matches();
    }

    /*
        强密码判断，需要密码包含大小写字母及数字且在8-20位
     */
    public static boolean isPassword(String passwd){
        if (StringUtils.isEmpty(passwd)){
            return false;
        }
//        boolean isDigit = false;//定义一个boolean值，用来表示是否包含数字
//        boolean isLetter = false;//定义一个boolean值，用来表示是否包含字母
//        for (int i = 0; i < passwd.length(); i++) {
//            if (Character.isDigit(passwd.charAt(i))) {   //用char包装类中的判断数字的方法判断每一个字符
//                isDigit = true;
//            } else if (Character.isLetter(passwd.charAt(i))) {  //用char包装类中的判断字母的方法判断每一个字符
//                isLetter = true;
//            }
//        }
//        String regex = "^[a-zA-Z0-9]{8,20}$";
//        boolean isRight = isDigit && isLetter && passwd.matches(regex);
//        return isRight;
        return true;
    }
}
