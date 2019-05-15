package com.seasun.management.util;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyEmojiUtil {
    /**
     * 将str中的emoji表情转为byte数组
     *
     * @param str:包含emoj的字符串
     * @return
     */
    public static String resolveToByteFromEmoji(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }

        Pattern pattern = Pattern
                .compile("[^(\u2E80-\u9FFF\\w\\s`~!@#\\$%\\^&\\*\\(\\)_+-？（）——=\\[\\]{}\\|;。，、《》”：；“！……’:'\"<,>\\.?/\\\\*)]");
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb2 = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb2, resolveToByte(matcher.group(0)));
        }
        matcher.appendTail(sb2);
        return sb2.toString();
    }

    /**
     * 将str中的byte数组类型的emoji表情转为正常显示的emoji表情
     *
     * @param str:数据库存储的emoji数组
     * @return 包含emoji的字符串
     */
    public static String resolveToEmojiFromByte(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }

        Pattern pattern2 = Pattern.compile("<:([[-]\\d*[,]]+):>");
        Matcher matcher2 = pattern2.matcher(str);
        StringBuffer sb3 = new StringBuffer();
        while (matcher2.find()) {
            matcher2.appendReplacement(sb3, resolveToEmoji(matcher2.group(0)));
        }
        matcher2.appendTail(sb3);
        return sb3.toString();
    }

    private static String resolveToByte(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }

        byte[] b = str.getBytes();
        StringBuffer sb = new StringBuffer();
        sb.append("<:");
        for (int i = 0; i < b.length; i++) {
            if (i < b.length - 1) {
                sb.append(Byte.valueOf(b[i]).toString() + ",");
            } else {
                sb.append(Byte.valueOf(b[i]).toString());
            }
        }
        sb.append(":>");
        return sb.toString();
    }

    private static String resolveToEmoji(String str) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        str = str.replaceAll("<:", "").replaceAll(":>", "");
        String[] s = str.split(",");
        byte[] b = new byte[s.length];
        for (int i = 0; i < s.length; i++) {
            b[i] = Byte.valueOf(s[i]);
        }
        return new String(b);
    }

    /**
     * 将byte数组类型的emoji表情替换为指定的字符串
     *
     * @param str
     * @param replace
     * @return
     */
    public static String replaceEmojiFromByte(String str, String replace) {
        if (StringUtils.isEmpty(str)) {
            return "";
        }
        return str.replaceAll("<:([[-]\\d*[,]]+):>", replace);
    }
}