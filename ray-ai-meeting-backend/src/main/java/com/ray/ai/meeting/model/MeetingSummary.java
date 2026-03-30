package com.ray.ai.meeting.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 会议总结数据模型
 * 包含待办事项、决策点和风险项
 *
 * @author Ray
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSummary {
    
    /**
     * 待办事项列表
     */
    private List<String> todos;
    
    /**
     * 决策点列表
     */
    private List<String> decisions;
    
    /**
     * 风险项列表
     */
    private List<String> risks;
}
