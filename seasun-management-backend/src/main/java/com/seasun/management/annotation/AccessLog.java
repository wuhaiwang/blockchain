package com.seasun.management.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLog {

    // 日志标示
    String tag() default "default";

    // 日志具体内容
    String message() default "default";

    /**
     * 功能: 通过该标志位，区分是否记录日志。默认为false(即不区分用户)
     * eg: true: 只记录boss触发的操作，false: 记录所有用户触发的操作
     */
    boolean onlyEnableBoss() default false;

}
