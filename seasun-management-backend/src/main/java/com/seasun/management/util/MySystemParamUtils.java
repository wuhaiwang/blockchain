package com.seasun.management.util;

import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.CfgSystemParamMapper;
import com.seasun.management.model.CfgSystemParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MySystemParamUtils {

    private static final Logger logger = LoggerFactory.getLogger(MySystemParamUtils.class);

    public interface Key {
        String TestModeFlag = "test-mode-flag";
        String TestModePw = "test-mode-pw";
        String PerformanceStartDay = "performance-start-day";
        String FixMemberRate = "fix-member-rate";
        String ReportBossRealTimeFLag = "report-boss-real-time-flag";
        String PerformanceSubmitStartDate = "performance-submit-start-date";
        String PmMilestoneShowYear = "pm_milestone_show_year";
        String NotInPerformanceWorkGroupList = "not-in-performance-work-group-list";
        String MaxMemberFlowReceiver = "max-member-flow-receiver";
        String FmPerfSubmitBarrierPlats = "fm-perf-submit-barrier-plats";
        String AnnualPartyStartFlag = "annual-party-start-flag";
        String BusModeFlag = "bus-mode-flag";
        String RtxTestSendUser = "rtx-test-send-user";
        String UserPsyStartFlag = "user-psy-start-flag";
        String ForumStartFlag = "forum-start-flag";
        String PerformanceNotifyDay = "performance-notify-day";
    }

    public interface DefaultValue {
        Boolean TestModeFlag = false;
        String TestModePw = "qwer1234";
        Integer PerformanceStartDay = 20;
        Double fixMemberRate = 0.9;
        Boolean reportBossRealTimeFlag = true;
        Date PerformanceSubmitStartDate = ReportHelper.getDate(2017, 9, 30).getTime();
        String MaxMemberFlowReceiver = "caosujuan";
        String FmPerfSubmitBarrierPlats = "1081";
    }

    public interface ValueType {
        String Boolean = "Boolean";
        String String = "String";
        String Integer = "Integer";
        String Double = "Double";
        String Date = "Date";
    }

    public static <T> T getSystemConfigWithDefaultValue(String key, T defaultValue) {
        Object value;
        CfgSystemParamMapper cfgSystemMapper = MyBeanUtils.getBean(CfgSystemParamMapper.class);
        CfgSystemParam cfgSystemParam = cfgSystemMapper.selectByName(key);
        if (null != cfgSystemParam) {
            try {
                switch (cfgSystemParam.getValueType()) {
                    case ValueType.Boolean:
                        value = Boolean.parseBoolean(cfgSystemParam.getValue());
                        break;
                    case ValueType.String:
                        value = cfgSystemParam.getValue();
                        break;
                    case ValueType.Integer:
                        value = Integer.parseInt(cfgSystemParam.getValue());
                        break;
                    case ValueType.Double:
                        value = Double.parseDouble(cfgSystemParam.getValue());
                        break;
                    case ValueType.Date:
                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                        value = format.parse(cfgSystemParam.getValue());
                        break;
                    default:
                        value = defaultValue;
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                value = defaultValue;
            }
        } else {
            value = defaultValue;
        }
        return (T) value;
    }
}
