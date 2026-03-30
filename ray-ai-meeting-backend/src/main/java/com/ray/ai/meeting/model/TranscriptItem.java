package com.ray.ai.meeting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 转录项数据模型
 * 表示单条语音转文字结果
 *
 * @author Ray
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TranscriptItem {
    
    /**
     * 说话人标识
     */
    private String speaker;
    
    /**
     * 转录文本
     */
    private String text;
    
    /**
     * 开始时间（秒）
     */
    private Double start;
    
    /**
     * 结束时间（秒）
     */
    private Double end;
}
