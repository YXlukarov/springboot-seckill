package com.jesper.seckill.service;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Title: AlipayService
 * @Auther: 皮蛋布丁
 * @Date: 2021/06/24/21:57
 * @Description:
 */
public interface AlipayService {

    /**
    * @Description: webPagePay web端订单支付
    * @Param: [subject 商品名称, outTradeNo 订单编号(唯一), totalAmount 订单价格]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/24 21:59
    */
    String webPagePay(String subject, String outTradeNo, String totalAmount);

    /**
    * @Description: notifyUrl 支付异步回调
    * @Param: [request]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/25 22:29
    */
    String notifyUrl(HttpServletRequest request);

    /**
    * @Description: returnUrl 支付同步回调
    * @Param: [request]
    * @return: java.util.Map<java.lang.String,java.lang.String[]>
    * @Author: 皮蛋布丁
    * @Date: 2021/6/25 22:29
    */
    Map<String, String[]> returnUrl(HttpServletRequest request);

    /**
    * @Description: tradeQuery 交易查询
    * @Param: [outTradeNo 订单编号, tradeNo 交易号]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/27 13:56
    */
    String tradeQuery(String outTradeNo,String tradeNo);

    /**
    * @Description: refund 退款
    * @Param: [outTradeNo 订单编号,
     *         tradeNo 交易号,
     *         refundAmount 退款金额,
     *         outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     *         ]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/27 14:31
    */
    String refund(String outTradeNo,String tradeNo,String refundAmount,String outRequestNo);

    /**
    * @Description: refundQuery 退款查询
    * @Param: [outTradeNo 订单编号（唯一）,
     *         tradeNo 交易号,
     *         outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     *         ]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/27 15:00
    */
    String refundQuery(String outTradeNo,String tradeNo,String outRequestNo);
}
