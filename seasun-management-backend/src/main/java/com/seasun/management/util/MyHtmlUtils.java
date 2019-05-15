package com.seasun.management.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

public class MyHtmlUtils {

    private static final Logger logger = LoggerFactory.getLogger(MyHtmlUtils.class);

    public static void generateHtml(HttpServletResponse response, List<String> content) {
        try {
            PrintWriter out = response.getWriter();
            response.setStatus(HttpServletResponse.SC_OK);
            content.forEach(s -> {
                if (s != null) {
                    logger.debug("s->:{0}", s);
                    out.append(s);
                }
            });

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("html生成失败：{0}", e.getMessage());
        }
    }
}
