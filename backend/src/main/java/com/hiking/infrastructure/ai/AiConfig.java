package com.hiking.infrastructure.ai;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

/**
 * AI 相关配置
 */
@Configuration
public class AiConfig {
    
    @Bean
    public WebClient aiWebClient(AiProperties properties) {
        return WebClient.builder()
                .baseUrl(properties.getApiUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getApiKey())
                .build();
    }
}
