package com.seasun.management.config;

import com.seasun.management.interceptor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Autowired
    AuthInterceptor authInterceptor;

    @Autowired
    SyncInterceptor syncInterceptor;

    @Autowired
    AppInterceptor appInterceptor;

    @Autowired
    PubApiInterceptor pubApiInterceptor;

    @Autowired
    InteriorApiInterceptor interiorApiInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/apis/auth/**").excludePathPatterns("/apis/auth/dsp-data-sync/**")
                .excludePathPatterns("/apis/auth/app/**").excludePathPatterns("/apis/auth/pub/**").excludePathPatterns("/apis/auth/user-salary-change/salary-key");
        registry.addInterceptor(syncInterceptor).addPathPatterns("/apis/auth/dsp-data-sync/**");
        registry.addInterceptor(appInterceptor).addPathPatterns("/apis/auth/app/**");
        registry.addInterceptor(pubApiInterceptor).addPathPatterns("/apis/auth/pub/**");
        registry.addInterceptor(interiorApiInterceptor).addPathPatterns("/apis/interior/pub/**");
    }

}
