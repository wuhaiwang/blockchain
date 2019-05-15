package com.seasun.management.helper.dataCenter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeDivisionHelper {

    private static final String beginTimeStamp = ":00";
    private static final String endTimeStamp = ":30";
    private static final String beginTheDayStamp = "00:00";
    private static final String endTheDayStamp = "24:00";

    /**
     * 将每个时间处理成 整点或整半点的时刻
     *
     * @param originTime 传入时间 eg:2017-05-17 02:15
     * @return 整理后的时间 eg:2017-05-17 02:30
     */
    public static String getFormatTime(String originTime) {
        String yMDString = originTime.substring(0, 11);

        String hString = originTime.substring(11, 13);
        String mString = originTime.substring(13);

        //如果是零点，转换成前一日24点
        if ((hString + mString).equals(beginTheDayStamp)) {
            int year = Integer.parseInt(originTime.substring(0, 4));
            int month = Integer.parseInt(originTime.substring(5, 7));
            int day = Integer.parseInt(originTime.substring(8, 10));
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month - 1, day);
            calendar.add(Calendar.DATE, -1);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date = simpleDateFormat.format(calendar.getTime());
            return date + " " + endTheDayStamp;
        }
        if (mString.compareTo(beginTimeStamp) > 0 && mString.compareTo(endTimeStamp) < 0) {
            return yMDString + hString + endTimeStamp;
        } else if (mString.compareTo(endTimeStamp) > 0) {
            if (Integer.parseInt(hString) + 1 >= 10) {
                return yMDString + (Integer.parseInt(hString) + 1) + beginTimeStamp;
            } else {
                return yMDString + "0" + (Integer.parseInt(hString) + 1) + beginTimeStamp;
            }
        }
        return originTime;
    }

    public static boolean isContainsLegitimateTime(String time) {
        Pattern pattern = Pattern.compile(":(0|3)0");
        Matcher matcher = pattern.matcher(time);
        return matcher.find();
    }

    public static String[] getLegitimateTimeStamp() {
        String[] result = new String[49];
        int j = 0;
        for (int i = 0; i < 24; i++) {
            if (i < 10) {
                result[j] = "0" + i + beginTimeStamp;
                result[j + 1] = "0" + i + endTimeStamp;
            } else {
                result[j] = i + beginTimeStamp;
                result[j + 1] = i + endTimeStamp;
            }
            j = j + 2;
        }
        result[48] = "24:00";
        return result;
    }

}
