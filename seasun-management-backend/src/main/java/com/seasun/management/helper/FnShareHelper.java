package com.seasun.management.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FnShareHelper {

    public static final List<String>  exportPlatShareConfigColums =new ArrayList<String>(){{
        add("EmployeeNo");
        add("UserName");
        add("Gender");
        add("WorkStatus");
        add("Post");
        add("InDate");
        add("WorkGroupName");
        add("PlatInShareProject");
        add("WorkDay");
        add("Remark");
    }};

    public static final Map<String,String> exportPlatShareConfigColumNameMap =new HashMap<String,String>(){{
        put("EmployeeNo","员工编号");
        put("UserName","姓名");
        put("Gender","性别");
        put("WorkStatus","状态");
        put("Post","岗位");
        put("InDate","入职日期");
        put("WorkGroupName","功能组");
        put("PlatInShareProject","平台-公司");
        put("WorkDay","工作日");
        put("Remark","备注说明");
    }};
}
