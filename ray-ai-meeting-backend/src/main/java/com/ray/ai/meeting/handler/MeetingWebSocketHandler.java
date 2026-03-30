package com.ray.ai.meeting.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ray.ai.meeting.model.TranscriptItem;
import com.ray.ai.meeting.model.WebSocketMessage;
import com.ray.ai.meeting.service.AudioProcessingService;
import com.ray.ai.meeting.service.SummaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会议 WebSocket 处理器
 * 处理音频流接收、转发和实时转录推送
 *
 * @author Ray
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MeetingWebSocketHandler extends AbstractWebSocketHandler {

    private final AudioProcessingService audioProcessingService;
    private final SummaryService summaryService;
    private final ObjectMapper objectMapper;

    private final Map<String, List<TranscriptItem>> sessionTranscripts = new ConcurrentHashMap<>();
    private final Map<String, String> sessionScenes = new ConcurrentHashMap<>();
    private final Map<String, byte[]> sessionAudioBuffers = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket 连接建立: {}, URI: {}", session.getId(), session.getUri());
        
        try {
            String scene = extractSceneFromSession(session);
            sessionScenes.put(session.getId(), scene);
            sessionTranscripts.put(session.getId(), new ArrayList<>());
            sessionAudioBuffers.put(session.getId(), new byte[0]);
            
            log.info("会议场景: {}, 连接成功", scene);
        } catch (Exception e) {
            log.error("初始化会话失败", e);
            throw e;
        }
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        byte[] audioData = message.getPayload().array();
        log.info("收到音频数据: {} bytes", audioData.length);
        
        try {
            // 异步处理音频，避免阻塞 WebSocket 线程
            processAudioAsync(session, audioData);
        } catch (Exception e) {
            log.error("处理音频数据失败", e);
        }
    }
    
    /**
     * 异步处理音频数据
     *
     * @param session WebSocket 会话
     * @param audioData 音频数据
     */
    private void processAudioAsync(WebSocketSession session, byte[] audioData) {
        // 使用虚拟线程或线程池异步处理
        new Thread(() -> {
            try {
                TranscriptItem transcript = audioProcessingService.processAudio(audioData);
                
                if (transcript != null && transcript.getText() != null && !transcript.getText().isEmpty()) {
                    sessionTranscripts.get(session.getId()).add(transcript);
                    
                    WebSocketMessage wsMessage = WebSocketMessage.builder()
                            .type("transcript")
                            .data(transcript)
                            .build();
                    
                    synchronized (session) {
                        if (session.isOpen()) {
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                        }
                    }
                    
                    // 每 10 条转录生成一次总结
                    if (sessionTranscripts.get(session.getId()).size() % 10 == 0) {
                        generateAndSendSummary(session);
                    }
                } else {
                    log.debug("识别结果为空，跳过");
                }
            } catch (Exception e) {
                log.error("异步处理音频失败", e);
            }
        }).start();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket 连接关闭: {}, 状态码: {}, 原因: {}", 
                session.getId(), status.getCode(), status.getReason());
        
        try {
            generateAndSendSummary(session);
        } catch (Exception e) {
            log.error("生成总结失败", e);
        }
        
        sessionTranscripts.remove(session.getId());
        sessionScenes.remove(session.getId());
        sessionAudioBuffers.remove(session.getId());
    }
    
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket 传输错误: {}, 异常: {}", session.getId(), exception.getMessage(), exception);
        super.handleTransportError(session, exception);
    }

    /**
     * 生成并发送会议总结
     *
     * @param session WebSocket 会话
     */
    private void generateAndSendSummary(WebSocketSession session) {
        try {
            List<TranscriptItem> transcripts = sessionTranscripts.get(session.getId());
            String scene = sessionScenes.get(session.getId());
            
            if (transcripts != null && !transcripts.isEmpty()) {
                var summary = summaryService.generateSummary(transcripts, scene);
                
                WebSocketMessage wsMessage = WebSocketMessage.builder()
                        .type("summary")
                        .data(summary)
                        .build();
                
                synchronized (session) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(wsMessage)));
                    }
                }
            }
        } catch (Exception e) {
            log.error("发送总结失败", e);
        }
    }

    /**
     * 从会话中提取场景参数
     *
     * @param session WebSocket 会话
     * @return 场景类型
     */
    private String extractSceneFromSession(WebSocketSession session) {
        String uri = session.getUri().toString();
        Map<String, String> params = UriComponentsBuilder.fromUriString(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap();
        
        return params.getOrDefault("scene", "finance");
    }
}
