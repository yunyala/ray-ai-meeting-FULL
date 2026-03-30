package com.ray.ai.meeting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WebSocket 消息封装
 * 统一的消息格式
 *
 * @author Ray
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    
    /**
     * 消息类型: transcript, summary, error
     */
    private String type;
    
    /**
     * 消息数据
     */
    private Object data;
}
