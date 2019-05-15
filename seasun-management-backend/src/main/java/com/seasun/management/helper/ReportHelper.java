package com.seasun.management.helper;

import com.seasun.management.model.Project;
import com.seasun.management.model.WorkGroup;

import java.math.BigDecimal;
import java.util.*;

public class ReportHelper {

    public static final Long OUTSOURCING_STAT_ID = 3700L;

    // 平台Id和对应的分摊项目Id
    public static final Map<Long, Long> PlatInShareProjectMap = Collections.unmodifiableMap(new HashMap<Long, Long>() {{
        put(Project.Id.TECGNOLOGY_CENTER, Project.Id.TECGNOLOGY_CENTER_SHARE_PROJECT);
        put(Project.Id.ART_CENTER, Project.Id.ART_CENTER_SHARE_PROJECT);
        put(Project.Id.QUALITY_CENTER, Project.Id.QUALITY_CENTER_SHARE_PROJECT);
        put(Project.Id.OPERATION_CENTER, Project.Id.OPERATION_CENTER_SHARE_PROJECT);
        put(Project.Id.WEB_DEVLOPMENT_CENTER, Project.Id.WEB_DEVLOPMENT_CENTER_SHARE_PROJECT);
    }});

    // 所有平台对应的分摊项目Id
    public static final List<Long> PlatShareProjectIds = new ArrayList<Long>( ){{
        add(Project.Id.PLAT_COMPANY);
        add(Project.Id.TECGNOLOGY_CENTER_SHARE_PROJECT);
        add(Project.Id.ART_CENTER_SHARE_PROJECT);
        add(Project.Id.QUALITY_CENTER_SHARE_PROJECT);
        add(Project.Id.OPERATION_CENTER_SHARE_PROJECT);
        add(Project.Id.WEB_DEVLOPMENT_CENTER_SHARE_PROJECT);
    }};

    public static final Long FnShareDataSummaryItemProjectId = 1L;

    public static final Map<String, String> FnStatReplaceNameMap = Collections.unmodifiableMap(new HashMap<String, String>() {{
        put("直接费用", "人力成本");
//        put("西山居总裁室", "西山居管理平台(含总裁办)");
    }});

    // 需要修改收入为产品运营收入的汇总项
    public static final List<Long> IncomeChangeProjects = Collections.unmodifiableList(new ArrayList<Long>() {{
        add(5080L); // 汇总-大西山居
        add(5081L); // 大西山居端游
        add(5082L); // 大西山居手游
    }});

    // 财务的广告费用
    public static final List<Long> sumProjects = Collections.unmodifiableList(new ArrayList<Long>() {{
        add(5080L); // 汇总-大西山居
        add(5081L); // 大西山居端游
        add(5082L); // 大西山居手游
    }});

    public static final String GuangGaoFei_Name = "广告费";
    public static final String LaoWuFei_Name = "劳务费(产品)";
    public static final String ShiChangfei_Name = "市场费";

    public static final Map<String, Long> statIdNameMap = new HashMap<String, Long>() {{
        put(GuangGaoFei_Name, 2601L);
        put(LaoWuFei_Name, 2603L);
        put(ShiChangfei_Name, 2602L);
    }};

    // 需要加总到别的项目费用里的关系配置
    public static final Map<Long, Long> AddToProjectMap = Collections.unmodifiableMap(new HashMap<Long, Long>() {{
        put(1100L, 5151L); // “亚丁-猎魔英雄” 加总到“猎魔英雄”
        put(5001L, 5103L); // “剑侠手游” 加总到“剑侠情缘手游”
        put(1156L, 5152L); // “成都-剑网三口袋版” 加总到“剑网3指尖江湖”
        put(1125L, 5139L); // “仙界” 加总到“TopGame”
    }});

    public static Float formatNumberByScale(Float source, int newScale) {
        if (null == source) {
            return 0F;
        }

        BigDecimal bg = new BigDecimal(source);
        return bg.setScale(newScale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static Float compareTwoDatesToDay(Date beginDate, Date endDate) {
        Float day = (float) (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    public static Boolean compareTwoDatesByGreaterOrEqual(Date beginDate, Date endDate) {
        Float day = compareTwoDatesToDay(beginDate, endDate);
        return day >= 0;

    }

    public static Boolean compareTwoDatesByGreater(Date beginDate, Date endDate) {
        Float day = compareTwoDatesToDay(beginDate, endDate);
        return day > 0;
    }

    public static Calendar getDateOnly() {
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now;
    }

    public static Calendar getDefaultDate() {
        Calendar defaultDate = ReportHelper.getDateOnly();
        defaultDate.set(1990, 0, 1);
        return defaultDate;
    }

    public static Calendar getDate(int year, int month, int day) {
        Calendar defaultDate = ReportHelper.getDateOnly();
        defaultDate.set(year, month - 1, day);
        return defaultDate;
    }

    public static String getWorkGroupFullPathName(WorkGroup workGroup, Map<Long, WorkGroup> allGroupMap) {
        String fullPathName = workGroup.getName();
        if (null != workGroup.getParent() && allGroupMap.containsKey(workGroup.getParent())) {
            WorkGroup parent = allGroupMap.get(workGroup.getParent());
            fullPathName = getWorkGroupFullPathName(parent, allGroupMap) + "/" + fullPathName;
        }
        return fullPathName;
    }
}
