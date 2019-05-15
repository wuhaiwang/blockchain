package com.seasun.management.util;

import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.model.FnWeekShareWorkdayStatus;
import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MyDateUtils {
    public final static String DATE_FORMAT_YYYY_MM_DD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 将日期格式化为App 端需要的字符串
     *
     * @param date
     * @return 年.月  eg：2017.01 、 2017.11
     */
    public static String formatDateToAppString(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        String result = year + "";
        return result + "." + (((month + "").toCharArray().length == 2) ? ((Integer) month).toString() : "0" + month);
    }

    /**
     * 获取当前年第一天
     *
     * @return Date
     */
    public static Date getCurrentYearFirst() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }

    /**
     * 获取当前年的之后的几年的第一天
     */
    public static Date getAfterCurrentFirst(int plusYear) {
        Calendar calendar = Calendar.getInstance();
        int afterYear = calendar.get(Calendar.YEAR) + plusYear;
        return getYearFirst(afterYear);
    }

    /**
     * 获取当前年之后几年的最后一天
     */
    public static Date getAfterCurrentLast(int plusYear) {
        Calendar calendar = Calendar.getInstance();
        int afterYear = calendar.get(Calendar.YEAR) + plusYear;
        return getYearLast(afterYear);
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取当年的最后一天
     *
     * @return
     */
    public static Date getCurrYearLast() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年最后一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        return calendar.getTime();
    }

    public static int getYearInteger(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 年月计算器
     *
     * @param value 进行运算的值
     */
    public static YearMonthDto yearMonthCalculation(Integer year, Integer month, int value) {
        YearMonthDto result = new YearMonthDto();
        int count = month + value;
        if (count > 12) {
            year++;
            month = count - 12;
        } else if (count < 1) {
            year--;
            month = count + 12;
        } else {
            month = count;
        }
        result.setYear(year);
        result.setMonth(month);

        return result;
    }

    /**
     * 时间格式化
     *
     * @param date
     * @param dateFormat
     * @return
     */
    public static String dateToString(Date date, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        return format.format(date);
    }

    /**
     * 106    * 将短时间格式字符串转换为时间 yyyy-MM-dd
     * 107    *
     * 108    * @param strDate
     * 109    * @return
     * 110
     */
    public static Date strToDate(String strDate,String format) {
        Date strtodate = null;
        if (StringUtils.isNotEmpty(strDate)) {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            ParsePosition pos = new ParsePosition(0);
            strtodate = formatter.parse(strDate, pos);
        }
        return strtodate;
    }

    /**
     * 年月日转换 yyMMdd->yy-MM-DD
     *
     * @param st_date
     * @return
     * */
    public static String getSt_date(String st_date) {
        return st_date.substring(0, 4) + "-" + st_date.substring(4, 6) + "-" + st_date.substring(6, 8);
    }

    /**
     * 比较两个日期，求天数
     * @param src
     * @param des
     * @return
     */
    public static long daysComparison(String src,String des){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        //跨年不会出现问题
        //如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 0
        long days = 0;
        try {
            Date fDate = sdf.parse(src);
            Date oDate=sdf.parse(des);
            days=(oDate.getTime()-fDate.getTime())/(1000*3600*24);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }

    /**
     * 获取当月的最后一天日期
     * @param year
     * @param month
     */
    public static List<Date> getLastDay(Integer year,Integer month){
        List<Date> list = new ArrayList<Date>();
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        list.add(firstDate);
        list.add(lastDate);
        return list;
    }

    /**
     * 根据年月获取当前月有多少天
     * @param year
     * @param month
     * @return
     */
    public static Integer geDaysByYYMM(Integer year,Integer month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month-1);//7月
        Integer maxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        return maxDate;
    }

    public static String getDateStrByFnWeekShareWorkdayStatus(FnWeekShareWorkdayStatus source){
        StringBuilder sb = new StringBuilder();
        sb.append(source.getYear()).append("年第").append(source.getWeek()).append("周");
        if (source.getMonth() != null) {
            sb.append("(").append(source.getYear()).append("年").append(source.getMonth()).append("月").append(source.getDay()).append("日 - ");
            if(source.getEndMonth()==1){
                sb.append(source.getYear()+1);
            }else{
                sb.append(source.getYear());
            }
            sb.append("年").append(source.getEndMonth()).append("月").append(source.getEndDay()).append("日)");
        }

        return sb.toString();
    }
}
