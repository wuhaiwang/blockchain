package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.seasun.management.constant.ApConstant;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.WXReturnFieldConstant;
import com.seasun.management.dto.SimpleUserOrderDto;
import com.seasun.management.dto.AppUserWXOrderSignDto;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserOrderMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserOrder;
import com.seasun.management.service.ApService;
import com.seasun.management.service.OrderErrorLogService;
import com.seasun.management.service.UserOrderService;
import com.seasun.management.util.MyAddressUtil;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.weixin.MyWXPayConfig;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserOrderServiceImpl implements UserOrderService {

    private static final Logger logger = LoggerFactory.getLogger(UserOrderServiceImpl.class);

    @Autowired
    private UserOrderMapper userOrderMapper;

    @Autowired
    private OrderErrorLogService orderErrorLogService;

    @Autowired
    private MyWXPayConfig myWXPayConfig;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApService apService;

    @Value("${wx.notify.url}")
    private String notifyUrl;

    @Override
    @Transactional
    public AppUserWXOrderSignDto createWXOrder(SimpleUserOrderDto simpleUserOrderDto) {
        long currentTimeMs = System.currentTimeMillis();
        User currentUser = MyTokenUtils.getCurrentUser();
        //本地订单号
        String localTradeNo = new SimpleDateFormat("yyyyMMddhhmmss").format(currentTimeMs) + currentUser.getId() + RandomStringUtils.randomAlphanumeric(8);
        UserOrder userOrder = new UserOrder();
        BeanUtils.copyProperties(simpleUserOrderDto, userOrder);
        userOrder.setLocalTradeNo(localTradeNo);
        String errorMessage = null;
        Map<String, String> respMap = null;

        try {
            //发起订单
            respMap = unifiedWXOrder(userOrder, myWXPayConfig);
            //判断连接是否超时 && 请求返回是否异常
            if (respMap != null && WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.return_code))) {
                //业务结果
                if (WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.result_code))) {
                    //验证签名
                    if (respMap.get(WXReturnFieldConstant.sign).equals(WXPayUtil.generateSignature(respMap, myWXPayConfig.getKey()))) {
                        long createOrderTimeMs = System.currentTimeMillis();
                        //保存订单信息
                        userOrder.setClientFullInfo(simpleUserOrderDto.toString());
                        userOrder.setUserId(currentUser.getId());
                        userOrder.setLocalTradeNo(localTradeNo);
                        userOrder.setCreateTime(new Date(createOrderTimeMs));
                        userOrder.setStatus(UserOrder.Status.initialized);
                        userOrder.setPrepayId(respMap.get(WXReturnFieldConstant.prepay_id));
                        userOrderMapper.insertSelective(userOrder);

                        //进行app签名加密
                        AppUserWXOrderSignDto result = new AppUserWXOrderSignDto();
                        result.setPrepayId(userOrder.getPrepayId());
                        result.setNonceStr(WXPayUtil.generateNonceStr());
                        result.setTimeStamp(createOrderTimeMs / 1000);
                        setWXAppPaySign(result, myWXPayConfig);
                        return result;
                    }
                    errorMessage = ErrorMessage.SIGN_DECRYPT_ERROR;
                }
            }
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }
        orderErrorLogService.insertOrderErrorLog(currentUser.getId(), JSONObject.toJSONString(simpleUserOrderDto), respMap == null ? ErrorMessage.WX_CONNECT_TIMEOUT : respMap.toString(), errorMessage);
        return null;
    }

    @Override
    @Transactional
    public String confirmWXOrder(String resp) throws Exception {

        // 解析结果存储在HashMap
        Map<String, String> respMap = null;
        try {
            respMap = WXPayUtil.xmlToMap(resp);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return "xml解析异常";
        }

        for (String key : respMap.keySet()) {
            logger.info(key + "========" + respMap.get(key));
        }

        //返回结果
        if (!WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.return_code))) {
            return respMap.get(WXReturnFieldConstant.return_msg);
        }
        //业务结果
        if (!WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.result_code))) {
            return respMap.get(WXReturnFieldConstant.err_code_des);
        }
        //验证签名
        if (!WXPayUtil.generateSignature(respMap, myWXPayConfig.getKey()).equals(respMap.get(WXReturnFieldConstant.sign))) {
            return ErrorMessage.SIGN_DECRYPT_ERROR;
        }

        //验证成功之后的业务处理
        String localTradeNo = respMap.get(WXReturnFieldConstant.out_trade_no);
        logger.info("===outTradeNo ===" + localTradeNo + "===begin confirm");
        UserOrder order = userOrderMapper.selectByLocalTradeNo(localTradeNo);
        if (order == null) {
            logger.info("===outTradeNo ===" + localTradeNo + "===confirm info error===" + ErrorMessage.USER_ORDER_PARAM_ERROR);
            return ErrorMessage.USER_ORDER_PARAM_ERROR;
        }

        if (!UserOrder.Status.payment.equals(order.getStatus())) {
            long currentTimeMillis = System.currentTimeMillis();
            order.setTransactionId(respMap.get(WXReturnFieldConstant.transaction_id));
            order.setStatus(UserOrder.Status.payment);
            order.setUpdateTime(new Date(currentTimeMillis));
            order.setCallbackTime(new Date(currentTimeMillis));
            order.setUpdateComment(respMap.toString());
            order.setOpenId(respMap.get(WXReturnFieldConstant.openid));
            order.setPayTime(respMap.get(WXReturnFieldConstant.time_end));
            userOrderMapper.updateByPrimaryKeySelective(order);
            logger.info("===outTradeNo ===" + order.getLocalTradeNo() + "===confirm info ===OK");
            //todo :年会弹幕,暂时放这
            User user = userMapper.selectByPrimaryKey(order.getUserId());
            apService.handleApDanmaku(user.getId(), String.format(ApConstant.AP_PAY_MESSAGE, user.getName(), ((float) order.getTotalFee() / 100) + "元"));
        }
        return null;
    }


    @Override
    public Map<String, String> wXOrderQuery(String transactionId, String outTradeNo) throws Exception {
        Map<String, String> result = new HashMap<>(1);
        WXPay wxpay = new WXPay(myWXPayConfig);

        Map<String, String> data = new HashMap<>();
        data.put(WXReturnFieldConstant.appid, myWXPayConfig.getAppID());
        data.put(WXReturnFieldConstant.mch_id, myWXPayConfig.getMchID());
        data.put(WXReturnFieldConstant.nonce_str, WXPayUtil.generateNonceStr());
        if (transactionId != null) {
            data.put(WXReturnFieldConstant.transaction_id, transactionId);
        } else {
            data.put(WXReturnFieldConstant.out_trade_no, outTradeNo);
        }
        data.put(WXReturnFieldConstant.sign, WXPayUtil.generateSignature(data, myWXPayConfig.getKey()));
        try {
            result = wxpay.orderQuery(data);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return result;
    }

    @Override
    public String orderRefund(String transactionId, String localTradeNo) {
        UserOrder selectOrder = null;

        //查询订单是否存在
        if (transactionId != null) {
            selectOrder = userOrderMapper.selectByTransactionId(transactionId);
        } else {
            selectOrder = userOrderMapper.selectByLocalTradeNo(localTradeNo);
        }

        if (selectOrder == null) {
            return ErrorMessage.USER_ORDER_PARAM_ERROR;
        }

        if (UserOrder.Status.refunding.equals(selectOrder.getStatus()) || UserOrder.Status.refunded.equals(selectOrder.getStatus())) {
            return "该订单已发起退款或退款已完成,请重新选择退款订单";
        }

        if (UserOrder.PayType.weixin.equals(selectOrder.getPayType())) {
            try {
                //发起退款请求
                Map<String, String> respMap = refundWXOrder(selectOrder, myWXPayConfig);
                //连接是否正常
                if (respMap == null) {
                    return ErrorMessage.WX_CONNECT_TIMEOUT;
                }
                //请求结果
                if (!WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.return_code))) {
                    return respMap.get(WXReturnFieldConstant.return_msg);
                }
                // 业务结果 && 验证签名
                if (!WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.result_code)) || !WXPayUtil.generateSignature(respMap, myWXPayConfig.getKey()).equals(respMap.get(WXReturnFieldConstant.sign))) {
                    return respMap.toString();
                }
                selectOrder.setStatus(UserOrder.Status.refunding);
                selectOrder.setUpdateTime(new Date());
                if (userOrderMapper.updateByPrimaryKeySelective(selectOrder) < 1) {
                    return ErrorMessage.PERSISTENT_LAYER_MESSAGE;
                }
                return null;
            } catch (Exception e) {
                return e.getMessage();
            }
        }
        return ErrorMessage.PARAM_ERROR_MESSAGE;
    }

    @Override
    //todo:此功能暂未完成
    public String refundedWXOrder(String resp) {
        try {
            Map<String, String> respMap = WXPayUtil.xmlToMap(resp);

            for (String key : respMap.keySet()) {
                logger.info(key + "========" + respMap.get(key));
            }
            //返回结果
            if (!WXPayConstants.SUCCESS.equals(respMap.get(WXReturnFieldConstant.return_code))) {
                return respMap.get(WXReturnFieldConstant.return_msg);
            }
            //解密
            String respxml = MyEncryptorUtils.decryptWXRefunded(respMap.get(WXReturnFieldConstant.req_info), myWXPayConfig.getKey());
            logger.info("decryptWXRefunded================" + respMap);
            Map<String, String> respDecryptMap = WXPayUtil.xmlToMap(respxml);

            for (String key : respDecryptMap.keySet()) {
                logger.info(key + "========" + respDecryptMap.get(key));
            }

            //业务结果
            if (!WXPayConstants.SUCCESS.equals(respDecryptMap.get(WXReturnFieldConstant.refund_status))) {
                return respDecryptMap.get(WXReturnFieldConstant.refund_status);
            }

            UserOrder order = userOrderMapper.selectByLocalTradeNo(respDecryptMap.get(WXReturnFieldConstant.out_trade_no));
            if (order == null) {
                return ErrorMessage.USER_ORDER_PARAM_ERROR;
            }

            if (!UserOrder.Status.refunded.equals(order.getStatus())) {
                order.setStatus(UserOrder.Status.refunded);
                order.setUpdateTime(new Date());
                userOrderMapper.updateByPrimaryKeySelective(order);
            }
            return null;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "参数格式校验错误";
    }

    @Override
    public Long getProductTotalFee(Long id) {
        return userOrderMapper.selectTotalFeeByProductId(id);
    }

    public Map<String, String> refundWXOrder(UserOrder order, MyWXPayConfig config) throws Exception {
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put(WXReturnFieldConstant.appid, config.getAppID());
        reqMap.put(WXReturnFieldConstant.mch_id, config.getMchID());
        reqMap.put(WXReturnFieldConstant.nonce_str, WXPayUtil.generateNonceStr());
        if (order.getTransactionId() != null) {
            reqMap.put(WXReturnFieldConstant.transaction_id, order.getTransactionId());
        } else {
            reqMap.put(WXReturnFieldConstant.out_trade_no, order.getLocalTradeNo());
        }
        reqMap.put(WXReturnFieldConstant.total_fee, order.getTotalFee().toString());
        reqMap.put(WXReturnFieldConstant.refund_fee, order.getTotalFee().toString());
        reqMap.put(WXReturnFieldConstant.out_refund_no, System.currentTimeMillis() + order.getUserId() + RandomStringUtils.randomAlphanumeric(8));
        WXPay wxPay = new WXPay(config, WXPayConstants.SignType.MD5);
        return wxPay.refund(reqMap);
    }

    public Map<String, String> unifiedWXOrder(UserOrder userOrder, MyWXPayConfig config) throws Exception {
        WXPay wxpay = new WXPay(config, WXPayConstants.SignType.MD5);
        Map<String, String> reqMap = new HashMap<>();
        //商品描述： APP名称-实际商品名称
        reqMap.put(WXReturnFieldConstant.body, UserOrder.ProductName.bonus);
        //本地订单号
        reqMap.put(WXReturnFieldConstant.out_trade_no, userOrder.getLocalTradeNo());
        //终端设备号(门店号或收银设备ID)，默认请传"WEB"
        reqMap.put(WXReturnFieldConstant.device_info, "WEB");
        //币种
        reqMap.put(WXReturnFieldConstant.fee_type, UserOrder.FeeType.CNY);
        //总金额，单位：分 todo:如果使用沙箱环境，设置金额参数必须参照微信公众号推荐的验收案例，否则金额异常)
        reqMap.put(WXReturnFieldConstant.total_fee, userOrder.getTotalFee().toString());
        //本机ip地址
        reqMap.put(WXReturnFieldConstant.spbill_create_ip, MyAddressUtil.getIpAddress());
        //回调url
        reqMap.put(WXReturnFieldConstant.notify_url, notifyUrl);
        //微信app支付方式： 写死为 APP
        reqMap.put(WXReturnFieldConstant.trade_type, "APP");
        //商品ID,自定义
        reqMap.put(WXReturnFieldConstant.product_id, UserOrder.ProductId.bonus.toString());
        //随机数
        reqMap.put(WXReturnFieldConstant.nonce_str, WXPayUtil.generateNonceStr());
        //签名
        reqMap.put(WXReturnFieldConstant.sign, WXPayUtil.generateSignature(reqMap, config.getKey()));
        //发起下单请求
        return wxpay.unifiedOrder(reqMap);
    }

    public void setWXAppPaySign(AppUserWXOrderSignDto appUserWXOrderSignDto, MyWXPayConfig config) throws Exception {
        Map<String, String> signMap = new HashMap<>();
        signMap.put(WXReturnFieldConstant.prepayid, appUserWXOrderSignDto.getPrepayId());
        signMap.put(WXReturnFieldConstant.noncestr, appUserWXOrderSignDto.getNonceStr());
        signMap.put(WXReturnFieldConstant.timestamp, appUserWXOrderSignDto.getTimeStamp().toString());
        signMap.put(WXReturnFieldConstant.appid, config.getAppID());
        signMap.put(WXReturnFieldConstant.partnerid, config.getMchID());
        //官方写死
        signMap.put("package", "Sign=WXPay");
        appUserWXOrderSignDto.setSign(WXPayUtil.generateSignature(signMap, config.getKey()));
    }

}
