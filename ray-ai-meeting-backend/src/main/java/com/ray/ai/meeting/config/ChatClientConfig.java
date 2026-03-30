package com.ray.ai.meeting.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient 配置类
 * 配置 Spring AI ChatClient Bean
 *
 * @author Ray
 * @since 1.0.0
 */
@Configuration
public class ChatClientConfig {

    /**
     * 创建 ChatClient Bean
     *
     * @param chatModel Spring AI ChatModel
     * @return ChatClient 实例
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
