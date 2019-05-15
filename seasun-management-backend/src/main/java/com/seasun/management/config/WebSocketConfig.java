package com.seasun.management.config;

import com.seasun.management.handler.ExcelImportHandler;
import com.seasun.management.interceptor.WebSocketHandshakeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;


@Configuration
@EnableWebSocket
public class WebSocketConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {

    @Autowired
    private ExcelImportHandler excelImportHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        Properties
        registry.addHandler(excelImportHandler, "/excel-import-socket")
                .addInterceptors(new WebSocketHandshakeInterceptor())
                .setAllowedOrigins("*");
    }
}
