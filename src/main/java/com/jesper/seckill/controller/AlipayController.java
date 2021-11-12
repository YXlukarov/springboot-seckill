package com.jesper.seckill.controller;

import com.jesper.seckill.service.AlipayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Title: AlipayController
 * @Auther: 皮蛋布丁
 * @Date: 2021/06/24/22:14
 * @Description:
 */
@Controller
@Slf4j
@RequestMapping("/alipay")
public class AlipayController {

    @Autowired
    AlipayService alipayService;

    @RequestMapping("/to_pay")
    public String To_pay() {
        return "order_pay";
    }

    /**
    * @Description: WebPagePay 发起支付
    * @Param: [outTradeNo, subject, totalAmount]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/24 22:22
    */
    @PostMapping("/pay")
    @ResponseBody
    public String WebPagePay(String outTradeNo,String subject,String totalAmount) {
        return alipayService.webPagePay(subject,outTradeNo,totalAmount);
    }

    /**
    * @Description: notifyUrl 异步通知，在这里处理后端商品信息和订单信息的修改
    * @Param: [request]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/25 22:53
    */
    @PostMapping("/notifyUrl")
    @ResponseBody
    public String notifyUrl(HttpServletRequest request) {
        return alipayService.notifyUrl(request);
    }

    /**
    * @Description: returnUrl 同步通知，返回一堆参数，可以考虑改成返回订单详情界面
    * @Param: [request]
    * @return: java.util.Map<java.lang.String,java.lang.String[]>
    * @Author: 皮蛋布丁
    * @Date: 2021/6/25 22:55
    */
    @GetMapping("/returnUrl")
    @ResponseBody
    public Map<String,String[]> returnUrl(HttpServletRequest request) {
        return alipayService.returnUrl(request);
    }

    /**
    * @Description: tradeQuery 交易查询
    * @Param: [outTradeNo 订单编号, tradeNo 交易号]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/27 14:08
    */
    @PostMapping("/tradeQuery")
    @ResponseBody
    public String tradeQuery(String outTradeNo,String tradeNo) {
        return alipayService.tradeQuery(outTradeNo, tradeNo);
    }

    /**
    * @Description: refund 退款
    * @Param: [outTradeNo 订单编号,
     *         tradeNo 交易号,
     *         refundAmount 退款金额,
     *         outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     *         ]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/27 14:44
    */
    @PostMapping("/refund")
    @ResponseBody
    public String refund(String outTradeNo,String tradeNo,String refundAmount,String outRequestNo) {
        return alipayService.refund(outTradeNo, tradeNo, refundAmount, outRequestNo);
    }

    /**
    * @Description: refundQuery 退款查询
    * @Param: [outTradeNo 订单编号（唯一）,
     *         tradeNo 交易号,
     *         outRequestNo 标识一次退款请求，同一笔交易多次退款需要保证唯一，如需部分退款，则此参数必传
     *         ]
    * @return: java.lang.String
    * @Author: 皮蛋布丁
    * @Date: 2021/6/27 15:04
    */
    @PostMapping("/refundQuery")
    @ResponseBody
    public String refundQuery(String outTradeNo,String tradeNo,String outRequestNo){
        return alipayService.refundQuery(outTradeNo, tradeNo, outRequestNo);
    }

}
