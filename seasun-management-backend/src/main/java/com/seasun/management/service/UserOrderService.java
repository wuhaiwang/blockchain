package com.seasun.management.service;


import com.seasun.management.dto.SimpleUserOrderDto;
import com.seasun.management.dto.AppUserWXOrderSignDto;

import java.util.Map;

public interface UserOrderService {

    AppUserWXOrderSignDto createWXOrder(SimpleUserOrderDto userOrder);

    String confirmWXOrder(String resp) throws Exception;

    Map<String,String> wXOrderQuery(String transactionId, String outTradeNo) throws Exception;

    String orderRefund(String transactionId,String localTradeNo);

    String refundedWXOrder(String resp);

    Long getProductTotalFee(Long id);
}
