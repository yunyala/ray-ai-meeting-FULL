package com.ray.ai.meeting.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 * 提供服务状态检查端点
 *
 * @author Ray
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * 健康检查端点
     *
     * @return 服务状态信息
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        log.info("健康检查请求");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Ray AI Meeting Backend");
        response.put("timestamp", LocalDateTime.now());
        response.put("version", "1.0.0");
        
        return response;
    }
}
