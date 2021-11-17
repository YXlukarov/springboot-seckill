package com.jesper.seckill.config;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created with IntelliJ IDEA.
 *
 * @Title: AlipayConfig
 * @Auther: 皮蛋布丁
 * @Date: 2021/06/24/21:42
 * @Description:支付宝配置类
 */
@Configuration
public class AlipayConfig {

    //@Value():将配置文件的值配置过来
    //appid
    @Value("${alipay.appId}")
    private String appId;
    //协议
    @Value("${alipay.protocol}")
    private String protocol;
    //网关
    @Value("${alipay.gatewayHost}")
    private String gatewayHost;
    //RSA2
    @Value("${alipay.signType}")
    private String signType;
    //私钥
    @Value("${alipay.merchantPrivateKey}")
    private String merchantPrivateKey;
    //支付宝公钥字符串即可
    @Value("${alipay.alipayPublicKey}")
    private String alipayPublicKey;
    //可设置异步通知接收服务地址
    @Value("${alipay.notifyUrl}")
    private String notifyUrl;

    /**
    * @Description: getAlipayConfig 1、设置参数（全局只需设置一次）
    * @Param: []
    * @return: com.alipay.easysdk.kernel.Config
    * @Author: 皮蛋布丁
    * @Date: 2021/6/24 21:52
    */
    @Bean
    public Config getAlipayConfig(){
        Config config=new Config();
        config.appId=appId;
        config.protocol=protocol;
        config.gatewayHost=gatewayHost;
        config.signType=signType;
        config.merchantPrivateKey=merchantPrivateKey;
        config.alipayPublicKey=alipayPublicKey;
        config.notifyUrl=notifyUrl;
        Factory.setOptions(config);
        return config;
    }
}
