package com.ray.ai.meeting.service;

import com.ray.ai.meeting.model.TranscriptItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 音频处理服务客户端
 * 封装语音识别调用逻辑
 *
 * @author Ray
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AudioProcessingService {

    private final AliyunASRService aliyunASRService;

    /**
     * 处理音频数据并获取转录结果
     *
     * @param audioData 音频字节数据
     * @return 转录项（返回第一条结果）
     */
    public TranscriptItem processAudio(byte[] audioData) {
        try {
            List<TranscriptItem> results = aliyunASRService.transcribe(audioData);
            
            if (results != null && !results.isEmpty()) {
                return results.get(0);
            } else {
                log.warn("未获取到识别结果");
                return null;
            }
        } catch (Exception e) {
            log.error("处理音频失败", e);
            return null;
        }
    }

    /**
     * 批量处理音频数据
     *
     * @param audioData 音频字节数据
     * @return 完整的转录项列表
     */
    public List<TranscriptItem> processAudioBatch(byte[] audioData) {
        return aliyunASRService.transcribe(audioData);
    }
}
