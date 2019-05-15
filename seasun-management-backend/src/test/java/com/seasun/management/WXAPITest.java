package com.seasun.management;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.seasun.management.constant.WXReturnFieldConstant;
import com.seasun.management.weixin.MyWXPayConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class WXAPITest {

    @Autowired
    private MyWXPayConfig myWXPayConfig;
    /*
    *  以下为微信提供的api测试，详细接口描述，请参考官方文档。
    *  api文档地址： https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=9_1
    **/


    @Test
    // 微信支付查询
    public void testWxPay() throws Exception {
//        WXPay wxpay = new WXPay(config, WXPayConstants.SignType.MD5, true); // 使用沙箱测试
        WXPay wxpay = new WXPay(myWXPayConfig);

        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "腾讯充值中心-QQ会员充值");
        data.put("out_trade_no", "2016090910595900000012");
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", "1");
        data.put("spbill_create_ip", "123.12.12.123");
        data.put("notify_url", "http://www.example.com/wxpay/notify");
        data.put("trade_type", "NATIVE");  // 此处指定为扫码支付
        data.put("product_id", "12");

        try {
            Map<String, String> resp = wxpay.unifiedOrder(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    // 微信订单查询
    public void testWxQuery() throws Exception {
        WXPay wxpay = new WXPay(myWXPayConfig);

        Map<String, String> data = new HashMap<String, String>();
        data.put("appid", myWXPayConfig.getAppID());
        data.put("mch_id", myWXPayConfig.getMchID());
        data.put("nonce_str", WXPayUtil.generateNonceStr());
        data.put("out_trade_no", "201801110922113404bO8QnXDo");
        data.put("sign", WXPayUtil.generateSignature(data, myWXPayConfig.getKey()));
        try {
            Map<String, String> resp = wxpay.orderQuery(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    // 微信退款，需要证书
    public void testWxRefundQuery() throws Exception {
        WXPay wxpay = new WXPay(myWXPayConfig);
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put(WXReturnFieldConstant.appid, myWXPayConfig.getAppID());
        reqMap.put(WXReturnFieldConstant.mch_id, myWXPayConfig.getMchID());
        reqMap.put(WXReturnFieldConstant.nonce_str, WXPayUtil.generateNonceStr());
        reqMap.put(WXReturnFieldConstant.total_fee, "1");
        reqMap.put(WXReturnFieldConstant.refund_fee, "1");
        reqMap.put(WXReturnFieldConstant.out_trade_no, "201801151046332937v9cXA2ua");
        reqMap.put(WXReturnFieldConstant.out_refund_no, "201801551046332937v9cXA2ua");
        //  reqMap.put(WXReturnFieldConstant.refund_desc,"商品已售完");
        reqMap.put("sign", WXPayUtil.generateSignature(reqMap, myWXPayConfig.getKey()));
        try {
            Map<String, String> resp = wxpay.refund(reqMap);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    // 下载微信账单
    public void testDownloadBill() throws Exception {
        WXPay wxpay = new WXPay(myWXPayConfig);

        Map<String, String> data = new HashMap<String, String>();
        data.put("bill_date", "20140603");
        data.put("bill_type", "ALL");

        try {
            Map<String, String> resp = wxpay.downloadBill(data);
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    // 微信的支付回调
    public void testWxPayCallback() throws Exception {
        String notifyData = "...."; // 支付结果通知的xml格式数据

        WXPay wxpay = new WXPay(myWXPayConfig);

        Map<String, String> notifyMap = WXPayUtil.xmlToMap(notifyData);  // 转换成map

        if (wxpay.isPayResultNotifySignatureValid(notifyMap)) {
            // 签名正确
            // 进行处理。
            // 注意特殊情况：订单已经退款，但收到了支付结果成功的通知，不应把商户侧订单状态从退款改成支付成功
        } else {
            // 签名错误，如果数据里没有sign字段，也认为是签名错误
        }
    }
}


