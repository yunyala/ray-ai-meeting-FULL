package com.ray.ai.meeting.service;

import com.ray.ai.meeting.model.MeetingSummary;
import com.ray.ai.meeting.model.TranscriptItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会议总结服务
 * 使用 Spring AI Alibaba 调用 LLM 生成会议总结
 *
 * @author Ray
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryService {

    private final ChatModel chatModel;
    private final ChatClient chatClient;

    /**
     * 生成会议总结
     *
     * @param transcripts 转录列表
     * @param scene 会议场景类型
     * @return 会议总结
     */
    public MeetingSummary generateSummary(List<TranscriptItem> transcripts, String scene) {
        String systemPrompt = getSystemPrompt(scene);
        String userContent = buildTranscriptText(transcripts);

        try {
            String response = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userContent)
                    .call()
                    .content();
            
            return parseSummary(response);
        } catch (Exception e) {
            log.error("生成总结失败", e);
            return MeetingSummary.builder()
                    .todos(new ArrayList<>())
                    .decisions(new ArrayList<>())
                    .risks(new ArrayList<>())
                    .build();
        }
    }

    /**
     * 根据场景获取系统提示词
     *
     * @param scene 场景类型
     * @return 系统提示词
     */
    private String getSystemPrompt(String scene) {
        return switch (scene) {
            case "finance" -> """
                你是一位专业的财务会议助手。请分析以下会议转录内容，提取关键信息：
                
                1. 待办事项（To-Do）：需要跟进的财务任务、对账项、审批事项
                2. 决策点（Decisions）：已达成的财务决策、预算批准、支付安排
                3. 风险项（Risks）：财务风险、合规问题、资金缺口
                
                请以以下格式输出：
                【待办事项】
                - 事项1
                - 事项2
                
                【决策点】
                - 决策1
                - 决策2
                
                【风险项】
                - 风险1
                - 风险2
                """;
            case "hr" -> """
                你是一位专业的人事招聘助手。请分析以下面试会议转录内容，提取关键信息：
                
                1. 待办事项（To-Do）：后续面试安排、背景调查、offer 准备
                2. 决策点（Decisions）：候选人评价、是否进入下一轮、薪资范围
                3. 风险项（Risks）：候选人风险点、团队匹配度问题、竞争对手挖角风险
                
                请以以下格式输出：
                【待办事项】
                - 事项1
                - 事项2
                
                【决策点】
                - 决策1
                - 决策2
                
                【风险项】
                - 风险1
                - 风险2
                """;
            default -> "请总结会议内容，提取待办事项、决策点和风险项。";
        };
    }

    /**
     * 构建转录文本
     *
     * @param transcripts 转录列表
     * @return 格式化的转录文本
     */
    private String buildTranscriptText(List<TranscriptItem> transcripts) {
        return transcripts.stream()
                .map(t -> String.format("[%s]: %s", t.getSpeaker(), t.getText()))
                .collect(Collectors.joining("\n"));
    }

    /**
     * 解析 LLM 返回的总结文本
     *
     * @param response LLM 响应
     * @return 结构化的会议总结
     */
    private MeetingSummary parseSummary(String response) {
        List<String> todos = new ArrayList<>();
        List<String> decisions = new ArrayList<>();
        List<String> risks = new ArrayList<>();

        String[] sections = response.split("【");
        
        for (String section : sections) {
            if (section.contains("待办事项")) {
                todos = extractItems(section);
            } else if (section.contains("决策点")) {
                decisions = extractItems(section);
            } else if (section.contains("风险项")) {
                risks = extractItems(section);
            }
        }

        return MeetingSummary.builder()
                .todos(todos)
                .decisions(decisions)
                .risks(risks)
                .build();
    }

    /**
     * 从文本段落中提取列表项
     *
     * @param section 文本段落
     * @return 列表项
     */
    private List<String> extractItems(String section) {
        return Arrays.stream(section.split("\n"))
                .filter(line -> line.trim().startsWith("-"))
                .map(line -> line.trim().substring(1).trim())
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }
}
