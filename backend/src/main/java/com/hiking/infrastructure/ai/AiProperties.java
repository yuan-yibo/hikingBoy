package com.hiking.infrastructure.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {
    
    /**
     * 是否启用AI
     */
    private boolean enabled = true;
    
    /**
     * API地址
     */
    private String apiUrl = "https://api.openai.com/v1/chat/completions";
    
    /**
     * API密钥
     */
    private String apiKey;
    
    /**
     * 模型名称
     */
    private String model = "gpt-3.5-turbo";
    
    /**
     * 温度参数（创意程度）
     */
    private double temperature = 0.8;
    
    /**
     * 最大token数
     */
    private int maxTokens = 500;
    
    /**
     * 超时时间（秒）
     */
    private int timeout = 30;
}
