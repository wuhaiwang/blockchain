package com.seasun.management.controller.finance;

import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.WXReturnFieldConstant;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.dto.IdValueDto;
import com.seasun.management.dto.SimpleUserOrderDto;
import com.seasun.management.dto.AppUserWXOrderSignDto;
import com.seasun.management.model.UserOrder;
import com.seasun.management.service.OrderErrorLogService;
import com.seasun.management.service.UserOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@RestController
public class UserOrderController {

    private static final Logger logger = LoggerFactory.getLogger(UserOrderController.class);

    @Autowired
    private UserOrderService userOrderService;

    @Autowired
    private OrderErrorLogService orderErrorLogService;

    @RequestMapping(value = "/apis/auth/app/user-order", method = RequestMethod.POST)
    public ResponseEntity<?> createUserOrder(@RequestBody SimpleUserOrderDto order) {
        logger.info("begin createUserOrder");
        AppUserWXOrderSignDto result = null;
        if (UserOrder.PayType.weixin.equals(order.getPayType())) {
            result = userOrderService.createWXOrder(order);
        }
        logger.info("end createUserOrder...");
        if (result == null) {
            return ResponseEntity.ok(new CommonAppResponse(ErrorCode.WXPAY_NETWORKCONNECT_ERROR, ErrorMessage.NETWOOK_ERROR));
        }
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/apis/auth/app/user-order", method = RequestMethod.DELETE)
    public ResponseEntity<?> userOrderRefund(@RequestBody UserOrder userOrder) {
        logger.info("begin userOrderRefund");
        String result = userOrderService.orderRefund(userOrder.getTransactionId(), userOrder.getLocalTradeNo());
        logger.info("end userOrderRefund...");
        if (result == null) {
            return ResponseEntity.ok(new CommonAppResponse(0, WXPayConstants.SUCCESS));
        }
        return ResponseEntity.ok(new CommonAppResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, result));
    }

    @RequestMapping(value = "/pub/wx/pay-notify", produces = "text/html;charset=utf-8")
    public String wXPayNotify(HttpServletRequest request) throws Exception {
        Map<String, String> repMap = new HashMap<>();
        logger.info("begin wXPayNotify...");
        String reqStr = null;
        String result = null;
        try {
            ServletInputStream inputStream = request.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            final StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line);
            }
            reqStr = stringBuffer.toString();

            if (inputStream != null) {
                inputStream.close();
            }
            bufferedReader.close();

            if (reqStr != null) {
                result = userOrderService.confirmWXOrder(reqStr);
            } else {
                result = ErrorMessage.PERSISTENT_LAYER_MESSAGE;
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        logger.info("end wXPayNotify...");

        if (result != null) {
            orderErrorLogService.insertOrderErrorLog(null, null, reqStr, result);
            repMap.put(WXReturnFieldConstant.return_code, WXPayConstants.FAIL);
            repMap.put(WXReturnFieldConstant.return_msg, result);
        } else {
            repMap.put(WXReturnFieldConstant.return_code, WXPayConstants.SUCCESS);
        }

        return WXPayUtil.mapToXml(repMap);
    }

    //todo:此功能暂未完成
    @RequestMapping(value = "/pub/wx/refund-notify", produces = "text/html;charset=utf-8")
    public String wXRefundNotify(HttpServletRequest request) throws Exception {
        Map<String, String> repMap = new HashMap<>();
        logger.info("begin wXRefundNotify...");
        ServletInputStream inputStream = request.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        final StringBuffer stringBuffer = new StringBuffer();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line);
        }
        String reqStr = stringBuffer.toString();
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (stringBuffer.length() > 0) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String result = userOrderService.refundedWXOrder(reqStr);
        logger.info("end wXRefundNotify...");

        if (result != null) {
            logger.info("end wXRefundNotifyFail=====" + result);
            orderErrorLogService.insertOrderErrorLog(null, null, reqStr, result);
            repMap.put(WXReturnFieldConstant.return_code, WXPayConstants.FAIL);
            repMap.put(WXReturnFieldConstant.return_msg, result);
        } else {
            repMap.put(WXReturnFieldConstant.return_code, WXPayConstants.SUCCESS);
        }
        return WXPayUtil.mapToXml(repMap);
    }

    @RequestMapping(value = "/pub/wx/query", method = RequestMethod.GET)
    public ResponseEntity<?> wXPayQuery(@RequestParam(required = false) String transactionId, @RequestParam(required = false) String outTradeNo) throws Exception {
        logger.info("begin wXPayQuery...");
        Map<String, String> result = userOrderService.wXOrderQuery(transactionId, outTradeNo);
        logger.info("end wXPayQuery...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/apis/auth/app/product-totalFee", method = RequestMethod.GET)
    public ResponseEntity<?> getProductTotalFee(@RequestParam Long id) {
        logger.info("begin getProductTotalFee...");
        Long result = userOrderService.getProductTotalFee(id);
        logger.info("end getProductTotalFee...");
        return ResponseEntity.ok(new CommonAppResponse(0, new IdValueDto(id, result.floatValue())));
    }

}
