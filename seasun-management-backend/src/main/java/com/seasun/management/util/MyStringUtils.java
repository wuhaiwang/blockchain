
package com.seasun.management.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.List;

public class MyStringUtils {
    public static String convertFirstCharToUppercase(String input) {
        if (input == null) {
            return "";
        }
        byte[] items = input.getBytes();
        items[0] = (byte) ((char) items[0] - 'a' + 'A');
        return new String(items);
    }

    public static List<String> listConvert(List<Long> list) {
        List<String> result = new ArrayList<>();
        for (Long x : list) {
            if (x != null) {
                result.add(x.toString());
            }
        }
        return result;
    }

    public static String[] listConvertArray(List<Long> list) {
        return listConvert(list).toArray(new String[list.size()]);
    }

    // 将驼峰风格替换为下划线风格
    public static String camelhumpToUnderline(String str) {
        Matcher matcher = Pattern.compile("[A-Z]").matcher(str);
        StringBuilder builder = new StringBuilder(str);
        for (int i = 0; matcher.find(); i++) {
            builder.replace(matcher.start() + i, matcher.end() + i, "_" + matcher.group().toLowerCase());
        }
        if (builder.charAt(0) == '_') {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    public static boolean isPhoneLegal(String str) {
        return isChinaPhoneLegal(str) || isHKPhoneLegal(str);
    }

    public static boolean isChinaPhoneLegal(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isHKPhoneLegal(String str) {
        String regExp = "^(5|6|8|9)\\d{7}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static boolean isIdCardOrPassport(String str) {
        String regIdCard = "^[0-9]{6,20}$";
        String regPasspost="^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        Pattern pIdCard = Pattern.compile(regIdCard);
        Pattern pPassPost=Pattern.compile(regPasspost);
        Matcher mIdcard = pIdCard.matcher(str);
        Matcher mPassport = pPassPost.matcher(str);
        return mIdcard.matches()|| mPassport.matches();
    }
}