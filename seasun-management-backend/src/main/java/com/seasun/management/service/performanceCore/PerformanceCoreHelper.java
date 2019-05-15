package com.seasun.management.service.performanceCore;

import com.seasun.management.util.MyStringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class PerformanceCoreHelper {

    public static <T> void processSubGroup(List<T> result, List<Long> subGroupIds, List<T> allMonthRecords) {
        List<Long> newSubGroups = new ArrayList<>();
        for (T temp : allMonthRecords) {
            try {
                Method workGroupIdGetMethod = temp.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("workGroupId"));
                Long workGroupId = (Long) workGroupIdGetMethod.invoke(temp);
                Method subPerfGroupGetMethod = temp.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("subGroup"));
                String subGroupIdStr = (String) subPerfGroupGetMethod.invoke(temp);
                Method parentGroupIdGetMethod = temp.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("parentGroup"));
                if (null != parentGroupIdGetMethod) {
                    Long parentGroupId = (Long) parentGroupIdGetMethod.invoke(temp);
                    if (null != parentGroupId && subGroupIds.contains(parentGroupId)) {
                        result.add(temp);
                    }
                }
                if (subGroupIds.contains(workGroupId)) {
                    result.add(temp); // 先加入结果列表

                    // 继续处理子组
                    if (null != subGroupIdStr && !subGroupIdStr.isEmpty()) {
                        String[] tempSubGroupIds = subGroupIdStr.split(",");
                        for (String tempGroupId : tempSubGroupIds) {
                            newSubGroups.add(Long.valueOf(tempGroupId));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (newSubGroups.size() == 0) {
            return;
        }
        processSubGroup(result, newSubGroups, allMonthRecords);
    }
}

