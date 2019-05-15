package com.seasun.management.util;

import java.net.URLEncoder;

public class URLUtil {

    public static String encodeURIComponent(String component)   {
        String result = null;
        try {
            result = URLEncoder.encode(component, "UTF-8")
                    .replaceAll("\\%28", "(")
                    .replaceAll("\\%29", ")")
                    .replaceAll("\\+", "%20")
                    .replaceAll("\\%27", "'")
                    .replaceAll("\\%21", "!")
                    .replaceAll("\\%7E", "~");
        } catch(Exception e){
            result = component;
        }
        return result;
    }
}
