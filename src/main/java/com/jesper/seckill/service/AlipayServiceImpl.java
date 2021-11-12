package com.jesper.seckill.service;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Title: AlipayServiceImpl
 * @Auther: 皮蛋布丁
 * @Date: 2021/06/24/22:00
 * @Description:
 */
@Service
@Slf4j
public class AlipayServiceImpl implements AlipayService {

    @Value("${alipay.returnUrl}")
    private String returnUrl;

    @Override
    public String webPagePay(String subject, String outTradeNo, String totalAmount) {
        //returnUrl:同步通知地址
        try {
            AlipayTradePagePayResponse response = Factory.Payment.Page().pay(subject, outTradeNo, totalAmount, returnUrl);
            if (ResponseChecker.success(response)) {
                log.info("订单支付调用成功！");
                return response.getBody();
            } else {
                log.error("订单支付调用失败！原因：" + response.getBody());
            }
        } catch (Exception e) {
            log.error("订单支付调用异常！原因：" + e.getMessage());
        }
        return null;
    }

    @Override
    public String notifyUrl(HttpServletRequest request) {
        Map<String,String> map = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = request.getParameter(key);
            map.put(key,value);
        }
        try {
            if (Factory.Payment.Common().verifyNotify(map)) {
                log.info("异步通知验签成功！");
                //验证用户的支付结果
                //trade_status：交易状态。交易目前所处的状态。
                String trade_status = request.getParameter("trade_status");
                if (("TRADE_SUCCESS").equals(trade_status)) {
                    log.info("支付交易成功！");
                    //TODO 更新订单支付状态...
                }

            } else {
                log.error("异步通知验签失败！");
                return "fail";
            }
        } catch (Exception e) {
            log.error("异步发生异常{}",e.getMessage());
            return "fail";
        }
        //程序执行完后必须打印输出“success”（不包含引号）。
        // 如果商户反馈给支付宝的字符不是 success 这7个字符，支付宝服务器会不断重发通知，直到超过 24 小时 22 分钟。
        // 一般情况下，25 小时以内完成 8 次通知（通知的间隔频率一般是：4m,10m,10m,1h,2h,6h,15h）。
        return "success";
    }

    @Override
    public Map<String, String[]> returnUrl(HttpServletRequest request) {
        log.info("同步通知叮铃铃");
        System.out.println(request.getParameterMap());
        return request.getParameterMap();
    }

    @Override
    public String tradeQuery(String outTradeNo, String tradeNo) {
        try {
            AlipayTradeQueryResponse response = Factory.Payment.Common().optional("trade_no", tradeNo).query(outTradeNo);
            return response.getHttpBody();
        } catch (Exception e) {
            log.error("交易查询调用发生异常，原因{}",e.getMessage());
        }
        return null;
    }

    @Override
    public String refund(String outTradeNo, String tradeNo, String refundAmount, String outRequestNo) {
        try {
            AlipayTradeRefundResponse response = Factory.Payment.Common()
                    .optional("trade_no", tradeNo)
                    .optional("out_request_no", outRequestNo)
                    .refund(outTradeNo, refundAmount);
            if (ResponseChecker.success(response)) {
                return response.getHttpBody();
            } else {
                log.error("退款调用失败，原因{}",response.getHttpBody());
            }
        } catch (Exception e) {
            log.error("退款调用发生异常，原因{}",e.getMessage());
        }
        return null;
    }

    @Override
    public String refundQuery(String outTradeNo, String tradeNo, String outRequestNo) {
        try {
            AlipayTradeFastpayRefundQueryResponse response = Factory.Payment.Common()
                    .optional("trade_no", tradeNo)
                    .queryRefund(outTradeNo, outRequestNo);
            return response.httpBody;
        } catch (Exception e) {
            log.error("退款查询发生异常，原因{}",e.getStackTrace() );
        }
        return null;
    }
}
