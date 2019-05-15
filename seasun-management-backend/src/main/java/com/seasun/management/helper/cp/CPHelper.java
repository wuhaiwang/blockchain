package com.seasun.management.helper.cp;

import com.seasun.management.model.cp.Ordissues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPHelper {

    public static final List<Integer> doneOrdissuesStatus = new ArrayList<Integer>() {{

        add(Ordissues.Status.claimed);
        add(Ordissues.Status.relatingOrdIssues);
    }};

    public static final List<Integer> confirmedOrdissuesStatus = new ArrayList<Integer>() {{
        add(Ordissues.Status.claimed);
        add(Ordissues.Status.relatingOrdIssues);
        addAll(doneOrdissuesStatus);
    }};

    public static final List<Integer> dingOrdissuesStatus = new ArrayList<Integer>() {
        {
            add(Ordissues.Status.test);
            add(Ordissues.Status.newOrder);
            add(Ordissues.Status.locked);
        }
    };

    public static final Map<Integer, String> ordissuesStatusVoMap = new HashMap<Integer, String>() {{
        put(Ordissues.Status.test, "测试");
        put(Ordissues.Status.newOrder, "新单");
        put(Ordissues.Status.locked, "锁定订单");
        put(Ordissues.Status.checked, "订单验收");
        put(Ordissues.Status.confirmed, "订单确认");
        put(Ordissues.Status.receiptConfirming, "发票发起");
        put(Ordissues.Status.claimed, "报销发起");
        put(Ordissues.Status.relatingOrdIssues, "关联单号");
        put(Ordissues.Status.cancelled, "已取消");
        put(Ordissues.Status.confirmFailed, "验收不过");
    }};
}
