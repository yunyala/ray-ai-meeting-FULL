package com.ray.ai.meeting.config;

import com.ray.ai.meeting.handler.MeetingWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket 配置类
 * 配置 WebSocket 端点和处理器
 *
 * @author Ray
 * @since 1.0.0
 */
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final MeetingWebSocketHandler meetingWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(meetingWebSocketHandler, "/ws/meeting")
                .setAllowedOrigins("*")
                .setAllowedOriginPatterns("*");
    }

    /**
     * 配置 WebSocket 容器
     * 增加消息缓冲区大小以支持音频数据传输
     *
     * @return ServletServerContainerFactoryBean
     */
    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        
        // 设置文本消息缓冲区大小为 1MB
        container.setMaxTextMessageBufferSize(1024 * 1024);
        
        // 设置二进制消息缓冲区大小为 1MB
        container.setMaxBinaryMessageBufferSize(1024 * 1024);
        
        // 设置会话空闲超时时间为 15 分钟
        container.setMaxSessionIdleTimeout(15 * 60 * 1000L);
        
        // 启用异步发送支持
        container.setAsyncSendTimeout(5000L);
        
        return container;
    }
}
