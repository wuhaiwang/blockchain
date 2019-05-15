package com.seasun.management.helper;

import java.util.HashMap;
import java.util.Map;

public class UserSalaryHelper {

    private static Map<String, String> columnHeader = new HashMap<>();

    static {
        columnHeader.put("name", "姓名");
        columnHeader.put("score", "分数");
        columnHeader.put("grade", "等级");
        columnHeader.put("workGroup", "工作组");
        columnHeader.put("manager", "主管");
        columnHeader.put("status", "状态");
        columnHeader.put("changeSalaryAmount", "调薪值");
        columnHeader.put("evaluateType", "预估");
    }

    public static Map<String, String> getColumnHeader() {
        return columnHeader;
    }

}
