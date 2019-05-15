package com.seasun.management.helper;

import com.seasun.management.dto.KeyValueDto;
import com.seasun.management.dto.PerformanceHistoryDto;
import com.seasun.management.dto.PerformanceWorkGroupDto;
import com.seasun.management.model.CfgPerfHideManagerComment;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.vo.MemberPerformanceAppVo;
import com.seasun.management.vo.SubPerformanceBaseVo;

import java.util.*;
import java.util.stream.Collectors;

public class UserPerformanceHelper {

    private static List<String> columns = new ArrayList<String>() {{
        add("userName");
        add("employeeNo");
        add("inDate");
        add("workAge");
        add("post");
        add("projectName");
        add("restTime");
        add("unReasonRestTime");
        add("finalPerformance");
        add("workContent");
        add("managerComment");
    }};

    public static List<String> getColumns() {
        return columns;
    }

    private static Map<String, String> columnHeaderMap = new HashMap<>();

    static {
        columnHeaderMap.put("userName", "姓名");
        columnHeaderMap.put("employeeNo", "工号");
        columnHeaderMap.put("inDate", "入职时间");
        columnHeaderMap.put("workAge", "工作年限");
        columnHeaderMap.put("post", "岗位");
//        columnHeaderMap.put("workGroupName", "工作组");
        columnHeaderMap.put("projectName", "部门(项目)");
        columnHeaderMap.put("restTime", "请假(小时)");
        columnHeaderMap.put("unReasonRestTime", "迟到或无请假销假(小时)");
        columnHeaderMap.put("finalPerformance", "本月绩效");
        columnHeaderMap.put("workContent", "工作内容");
        columnHeaderMap.put("managerComment", "完成情况与评语");
//        columnHeaderMap.put("selfComment", "自我评价");
//        columnHeaderMap.put("monthGoal", "本月目标");
    }

    public static Map<String, String> getColumnHeaderMap() {
        return columnHeaderMap;
    }

    public interface ExcelTableHeader {
        String userName = "姓名";
        String employeeNo = "工号";
        String finalPerformance = "绩效";
        String monthGoal = "工作内容";
        String managerComment = "完成情况与评语";
    }

    private static Map<String, String> subMemberStatusChangeMap = new HashMap<>();

    static {
        subMemberStatusChangeMap.put(UserPerformance.Status.unsubmitted, MemberPerformanceAppVo.Status.unsubmitted);
        subMemberStatusChangeMap.put(UserPerformance.Status.submitted, MemberPerformanceAppVo.Status.submitted);
        subMemberStatusChangeMap.put(UserPerformance.Status.assessed, MemberPerformanceAppVo.Status.assessed);
        subMemberStatusChangeMap.put(UserPerformance.Status.locked, MemberPerformanceAppVo.Status.assessed);
        subMemberStatusChangeMap.put(UserPerformance.Status.complete, MemberPerformanceAppVo.Status.complete);
    }

    public static Map<String, String> getSubMemberStatusChangeMap() {
        return subMemberStatusChangeMap;
    }

    private static Map<String, Integer> subMemberStatusSortMap = new HashMap<>();

    static {
        subMemberStatusSortMap.put(MemberPerformanceAppVo.Status.unsubmitted, 1);
        subMemberStatusSortMap.put(MemberPerformanceAppVo.Status.submitted, 0);
        subMemberStatusSortMap.put(MemberPerformanceAppVo.Status.assessed, 2);
        subMemberStatusSortMap.put(MemberPerformanceAppVo.Status.complete, 4);
        subMemberStatusSortMap.put(MemberPerformanceAppVo.Status.confirmed, 3);
    }

    public static Map<String, Integer> getSubMemberStatusSortMap() {
        return subMemberStatusSortMap;
    }

    public static boolean isHideManagerComment(List<CfgPerfHideManagerComment> perfHideManagerComments, List<PerformanceWorkGroupDto> performanceWorkGroups,
                                               Long perfWorkGroupId, Integer year, Integer month) {
        Map<Long, CfgPerfHideManagerComment> perfHideManagerCommentMap = perfHideManagerComments.stream().filter(c -> year.equals(c.getYear()) && month.equals(c.getMonth())).collect(Collectors.toMap(c -> c.getPerfWorkGroupId(), c -> c));
        Map<Long, Long> perfWorkGroupParentMap = performanceWorkGroups.stream().filter(g -> null != g.getParent()).collect(Collectors.toMap(g -> g.getId(), g -> g.getParent()));
        return isHidePerfWorkGroup(perfHideManagerCommentMap, perfWorkGroupParentMap, perfWorkGroupId);
    }

    private static boolean isHidePerfWorkGroup(Map<Long, CfgPerfHideManagerComment> perfHideManagerCommentMap, Map<Long, Long> perfWorkGroupParentMap, Long perfWorkGroupId) {
        boolean isHide = perfHideManagerCommentMap.containsKey(perfWorkGroupId);
        if (!isHide && perfWorkGroupParentMap.containsKey(perfWorkGroupId) && null != perfWorkGroupParentMap.get(perfWorkGroupId)) {
            isHide = isHidePerfWorkGroup(perfHideManagerCommentMap, perfWorkGroupParentMap, perfWorkGroupParentMap.get(perfWorkGroupId));
        }
        return isHide;
    }

    public interface SearchFilter {
        String all = "all";
        String unsubmitted = "unsubmitted";
        String submitted = "submitted";
        String invalided = "invalided";
        String assessed = "assessed";
        String fixmember = "fixmember";
        String s = "s";
        String a = "a";
        String b = "b";
        String c = "c";
    }

    public static List<KeyValueDto> getCurrentFilter(boolean projectConfirmFlag) {

        List<KeyValueDto> keyValueDtos = new ArrayList<KeyValueDto>() {{
            add(new KeyValueDto() {{
                setKey(SearchFilter.all);
                setValue("全员");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.submitted);
                setValue("待评定");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.unsubmitted);
                setValue("未提交");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.invalided);
                setValue("不参与");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.assessed);
                setValue("已评定");

            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.s);
                setValue("S");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.a);
                setValue("A");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.b);
                setValue("B");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.c);
                setValue("C");
            }});
            if (projectConfirmFlag) {
                add(new KeyValueDto() {{
                    setKey(SearchFilter.fixmember);
                    setValue("固化成员");
                }});
            }
        }};
        return keyValueDtos;
    }

    public static List<KeyValueDto> getHistoryFilter(boolean projectConfirmFlag) {
        List<KeyValueDto> keyValueDtos = new ArrayList<KeyValueDto>() {{
            add(new KeyValueDto() {{
                setKey(SearchFilter.all);
                setValue("全员");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.invalided);
                setValue("不参与");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.s);
                setValue("S");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.a);
                setValue("A");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.b);
                setValue("B");
            }});
            add(new KeyValueDto() {{
                setKey(SearchFilter.c);
                setValue("C");
            }});
            if (projectConfirmFlag) {
                add(new KeyValueDto() {{
                    setKey(SearchFilter.fixmember);
                    setValue("固化成员");
                }});
            }
        }};
        return keyValueDtos;
    }

    /**
     * 计算锁定月，给web用。
     *
     * @param originalHistoryList
     * @return
     */
    public static int getSelectIndex(List<SubPerformanceBaseVo.HistoryInfo> originalHistoryList, Integer uiYear, Integer uiMonth) {

        int selectIndex = -1;
        String selectStatus = null;

        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;

        // 获取当前月的状态
        SubPerformanceBaseVo.HistoryInfo currentMonthInfo = null;
        int currentMonthIndex = 0;
        for (int i = 0; i < originalHistoryList.size(); i++) {
            SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
            if (history.getYear().intValue() == nowYear && history.getMonth().intValue() == nowMonth) {
                currentMonthInfo = history;
                currentMonthIndex = i;
                break;
            }
        }

        // 1. 找到锁定月  todo: 这里存在重复逻辑
        // 指定了月份
        if (uiYear != null) {
            // 遍历，并设置index
            for (int i = 0; i < originalHistoryList.size(); i++) {
                SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
                if (history.getYear().intValue() == uiYear && history.getMonth().intValue() == uiMonth) {
                    selectIndex = i;
                    selectStatus = history.getStatus();
                    break;
                }
            }
        }
        // 未指定月份
        else {
            // 若遍历月中存在“未完成”，则找到第一个未完成的，该月即为“锁定月”，并直接返回。
            for (int i = 0; i < originalHistoryList.size(); i++) {
                SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
                if (SubPerformanceBaseVo.HistoryInfo.Status.delay.equals(history.getStatus())
                        || SubPerformanceBaseVo.HistoryInfo.Status.processing.equals(history.getStatus())) {
                    selectIndex = i;
                    selectStatus = history.getStatus();
                    break;
                }
            }


            // 若找不到“未完成”，根据当前月状态，确定“锁定月”
            if (selectIndex == -1) {
                // 若当前月“未开始”，则上一个月为“锁定月”
                if (SubPerformanceBaseVo.HistoryInfo.Status.waitingForStart.equals(currentMonthInfo.getStatus())) {

                    // start : modify by xionghaitao : 解决没有绩效数据时，数组越界的bug
                    // 1.正常匹配到
                    if (currentMonthIndex > 0) {
                        selectIndex = currentMonthIndex - 1;
                    }
                    // 2. 特殊情况：首次开启绩效，且当前月份为1月，则第一个月份被选中。
                    else {
                        selectIndex = 0;
                    }
                    // end modify by xionghaitao
                    selectStatus = originalHistoryList.get(selectIndex).getStatus();;
                }
                // 否则，当前月为“锁定月”
                else {
                    selectIndex = currentMonthIndex;
                    selectStatus = currentMonthInfo.getStatus();
                }
            }
        }

        // 特殊处理：选中月是null，则强制选中为“未开始”月。
        if (selectStatus == null) {
            for (int i = 0; i < originalHistoryList.size(); i++) {
                if (SubPerformanceBaseVo.HistoryInfo.Status.waitingForStart.equals(originalHistoryList.get(i).getStatus())) {
                    selectIndex = i;
                    break;
                }
            }
        }
        return selectIndex;
    }


    /**
     * 计算返回的history额外信息（startIndex,endIndex,selectIndex），用于app端的时间轴显示
     *
     * @param originalHistoryList
     * @param startYear
     * @param startMonth
     * @param endYear
     * @param endMonth
     * @return
     */
    public static PerformanceHistoryDto buildHistoryTimeLineIndex(
            Integer uiYear, Integer uiMonth,
            List<SubPerformanceBaseVo.HistoryInfo> originalHistoryList,
            Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {

        PerformanceHistoryDto result = new PerformanceHistoryDto();
        result.setSelectIndex(0);
        result.setStartIndex(0);
        result.setEndIndex(0);

        int selectYear = 0;
        int selectMonth = 0;
        String selectStatus = null;

        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;

        // 获取当前月的状态
        SubPerformanceBaseVo.HistoryInfo currentMonthInfo = null;
        int currentMonthIndex = 0;
        for (int i = 0; i < originalHistoryList.size(); i++) {
            SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
            if (history.getYear().intValue() == nowYear && history.getMonth().intValue() == nowMonth) {
                currentMonthInfo = history;
                currentMonthIndex = i;
                break;
            }
        }

        // 1. 找到锁定月
        // 指定了月份
        if (uiYear != null) {
            // 遍历，并设置index
            for (int i = 0; i < originalHistoryList.size(); i++) {
                SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
                if (history.getYear().intValue() == uiYear && history.getMonth().intValue() == uiMonth) {
                    result.setSelectIndex(i);
                    selectYear = history.getYear();
                    selectMonth = history.getMonth();
                    selectStatus = history.getStatus();
                    break;
                }
            }
        }
        // 未指定月份
        else {

            // 若遍历月中存在“未完成”，则找到第一个未完成的，该月即为“锁定月”，并直接返回。
            for (int j = 0; j < originalHistoryList.size(); j++) {
                if (SubPerformanceBaseVo.HistoryInfo.Status.delay.equals(originalHistoryList.get(j).getStatus())
                        || SubPerformanceBaseVo.HistoryInfo.Status.processing.equals(originalHistoryList.get(j).getStatus())) {
                    result.setSelectIndex(j);
                    selectYear = originalHistoryList.get(j).getYear();
                    selectMonth = originalHistoryList.get(j).getMonth();
                    selectStatus = originalHistoryList.get(j).getStatus();
                    break;
                }
            }

            // 若找不到“未完成”，根据当前月状态，确定“锁定月”
            if (selectYear == 0) {
                // 若当前月“未开始”，则上一个月为“锁定月”
                if (SubPerformanceBaseVo.HistoryInfo.Status.waitingForStart.equals(currentMonthInfo.getStatus())) {

                    // start : modify by xionghaitao : 解决没有绩效数据时，数组越界的bug
                    int selectIndex = 0;
                    // 1.正常匹配到
                    if (currentMonthIndex > 0) {
                        selectIndex = currentMonthIndex - 1;
                    }
                    // 2. 特殊情况：首次开启绩效，且当前月份为1月，则第一个月份被选中。
                    else {
                        selectIndex = 0;
                    }
                    // end modify by xionghaitao
                    result.setSelectIndex(selectIndex);
                    selectYear = originalHistoryList.get(selectIndex).getYear();
                    selectMonth = originalHistoryList.get(selectIndex).getMonth();
                    selectStatus = originalHistoryList.get(selectIndex).getStatus();
                }
                // 否则，当前月为“锁定月”
                else {
                    result.setSelectIndex(currentMonthIndex);
                    selectYear = currentMonthInfo.getYear();
                    selectMonth = currentMonthInfo.getMonth();
                    selectStatus = currentMonthInfo.getStatus();
                }
            }
        }

        // 特殊处理：选中月是null，则强制选中为“未开始”月。
        if (selectStatus == null) {
            for (int i = 0; i < originalHistoryList.size(); i++) {
                if (SubPerformanceBaseVo.HistoryInfo.Status.waitingForStart.equals(originalHistoryList.get(i).getStatus())) {
                    result.setSelectIndex(i);
                    break;
                }
            }
        }


        // 2. 找到“起点月”
        // 若未指定了“起点月”，则取“锁定月”和“当前月”的最小值
        if (startMonth == null) {
            // 锁定月 晚于 当前年的1月份
            if (selectYear == nowYear && selectMonth >= 1) {
                startYear = selectYear;
                startMonth = 1;
            }
            // 锁定月 早于 当前年的1月份
            else {
                startYear = selectYear;
                startMonth = selectMonth;
            }
        }
        // 遍历，并设置index
        for (int i = 0; i < originalHistoryList.size(); i++) {
            SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
            if (history.getYear().intValue() == startYear && history.getMonth().intValue() == startMonth) {
                result.setStartIndex(i);
                break;
            }
        }

        // 3. 找到“终点月”
        // 若未指定“终点月”，则为当前年的最后一个月
        if (endMonth == null) {
            endYear = nowYear;
            endMonth = 12;
        }
        // 遍历，并设置index
        for (int i = 0; i < originalHistoryList.size(); i++) {
            SubPerformanceBaseVo.HistoryInfo history = originalHistoryList.get(i);
            if (history.getYear().intValue() == endYear && history.getMonth().intValue() == endMonth) {
                result.setEndIndex(i);
                break;
            }
        }

        return result;
    }


    public static boolean isDelay(int targetYear, int targetMonth, int startDay) {
        Calendar targetDate = Calendar.getInstance();
        //设置年份
        targetDate.set(Calendar.YEAR, targetYear);
        //设置月份
        targetDate.set(Calendar.MONTH, targetMonth - 1);
        //获取该月最大天数
        int lastDay = targetDate.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        targetDate.set(Calendar.DAY_OF_MONTH, lastDay);
        Calendar nowDate = Calendar.getInstance();
        long offset = calcDayOffset(targetDate.getTime(), nowDate.getTime());
        return offset >= startDay;
    }

    public static int calcDayOffset(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);

        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {  //同一年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
                    timeDistance += 366;
                } else {  //不是闰年

                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else { //不同年
            return day2 - day1;
        }
    }

    public static int getStrictType(List<Integer> strictNums) {

        // 有一个为严格则为严格
        for (Integer strictNum : strictNums) {
            if (strictNum.equals(PerformanceWorkGroup.GroupStrictType.strictNum)) {
                return PerformanceWorkGroup.GroupStrictType.strictNum;
            }
        }

        // 剩下的就只有：非严格 和 温和。 若其中有一个温和，则返回温和。
        if (strictNums.stream().anyMatch(s -> s.equals(PerformanceWorkGroup.GroupStrictType.normalNum))) {
            return PerformanceWorkGroup.GroupStrictType.normalNum;
        }

        // 剩下的都是非严格，则返回非严格。
        return PerformanceWorkGroup.GroupStrictType.unStrictNum;
    }


}
