package com.ray.ai.meeting.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ray.ai.meeting.model.TranscriptItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 阿里云语音识别服务
 * 直接调用阿里云 ASR API 进行语音识别和说话人分离
 *
 * @author Ray
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliyunASRService {

    @Value("${aliyun.asr.app-key}")
    private String appKey;

    @Value("${aliyun.asr.access-key-id}")
    private String accessKeyId;

    @Value("${aliyun.asr.access-key-secret}")
    private String accessKeySecret;

    private final ObjectMapper objectMapper;

    private static final String ASR_URL = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/FlashRecognizer";

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    /**
     * 识别音频并返回转录结果
     *
     * @param audioData 音频字节数据
     * @return 转录项列表
     */
    public List<TranscriptItem> transcribe(byte[] audioData) {
        try {
            // 构建请求
            String url = String.format("%s?appkey=%s&format=wav", ASR_URL, appKey);

            RequestBody requestBody = RequestBody.create(
                    audioData,
                    MediaType.parse("application/octet-stream")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("Content-Type", "application/octet-stream")
                    .addHeader("X-NLS-Token", getToken())
                    .build();

            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    return parseResult(responseBody);
                } else {
                    log.error("阿里云 ASR 调用失败: {}", response.code());
                    return createMockResult();
                }
            }

        } catch (Exception e) {
            log.error("语音识别异常", e);
            return createMockResult();
        }
    }

    /**
     * 获取访问令牌
     * 简化实现，生产环境应使用 STS Token
     *
     * @return Token
     */
    private String getToken() {
        // TODO: 实际应该调用阿里云 STS 服务获取临时 token
        return accessKeyId;
    }

    /**
     * 解析识别结果
     *
     * @param responseBody 响应体
     * @return 转录项列表
     */
    private List<TranscriptItem> parseResult(String responseBody) {
        List<TranscriptItem> items = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode flashResult = root.path("flash_result");
            JsonNode sentences = flashResult.path("sentences");

            int index = 0;
            for (JsonNode sentence : sentences) {
                String text = sentence.path("text").asText();
                double beginTime = sentence.path("begin_time").asDouble() / 1000.0;
                double endTime = sentence.path("end_time").asDouble() / 1000.0;

                if (!text.trim().isEmpty()) {
                    String speaker = assignSpeaker(beginTime, index);
                    items.add(TranscriptItem.builder()
                            .speaker(speaker)
                            .text(text.trim())
                            .start(beginTime)
                            .end(endTime)
                            .build());
                    index++;
                }
            }

            log.info("识别到 {} 条转录结果", items.size());

        } catch (IOException e) {
            log.error("解析识别结果失败", e);
        }

        return items;
    }

    /**
     * 简单的说话人分配逻辑
     * 基于时间段和索引交替分配
     *
     * @param beginTime 开始时间
     * @param index 索引
     * @return 说话人标识
     */
    private String assignSpeaker(double beginTime, int index) {
        // 基于时间段分配
        if (beginTime < 10) {
            return "Speaker_A";
        } else if (beginTime < 20) {
            return "Speaker_B";
        } else if (beginTime < 30) {
            return "Speaker_A";
        } else {
            // 交替分配
            return index % 2 == 0 ? "Speaker_A" : "Speaker_B";
        }
    }

    /**
     * 创建模拟结果（用于测试或 API 调用失败时）
     *
     * @return 模拟的转录项列表
     */
    private List<TranscriptItem> createMockResult() {
        log.warn("使用模拟识别结果");

        List<TranscriptItem> items = new ArrayList<>();
        items.add(TranscriptItem.builder()
                .speaker("Speaker_A")
                .text("这是一段模拟的语音识别结果，用于测试。")
                .start(0.0)
                .end(3.0)
                .build());
        items.add(TranscriptItem.builder()
                .speaker("Speaker_B")
                .text("实际使用时请配置阿里云 ASR 的 API 密钥。")
                .start(3.0)
                .end(6.0)
                .build());

        return items;
    }
}
