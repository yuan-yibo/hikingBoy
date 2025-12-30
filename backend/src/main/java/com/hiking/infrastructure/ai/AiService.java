package com.hiking.infrastructure.ai;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * AI 服务 - 调用 OpenAI 兼容 API 生成文案
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {
    
    private final WebClient aiWebClient;
    private final AiProperties properties;
    
    /**
     * 调用AI生成文案
     */
    public String generateContent(String prompt) {
        if (!properties.isEnabled()) {
            log.warn("AI service is disabled, returning fallback content");
            return generateFallbackContent();
        }
        
        try {
            Map<String, Object> requestBody = buildRequestBody(prompt);
            
            String response = aiWebClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(properties.getTimeout()))
                    .map(this::extractContent)
                    .onErrorResume(e -> {
                        log.error("AI API call failed: {}", e.getMessage());
                        return Mono.just(generateFallbackContent());
                    })
                    .block();
            
            return response != null ? response : generateFallbackContent();
        } catch (Exception e) {
            log.error("AI generation failed", e);
            return generateFallbackContent();
        }
    }
    
    /**
     * 加载prompt模板
     */
    public String loadPromptTemplate(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("Failed to load prompt template: {}", path, e);
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }
    
    private Map<String, Object> buildRequestBody(String prompt) {
        return Map.of(
                "model", properties.getModel(),
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", properties.getTemperature(),
                "max_tokens", properties.getMaxTokens()
        );
    }
    
    @SuppressWarnings("unchecked")
    private String extractContent(Map<String, Object> response) {
        try {
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map<String, Object> firstChoice = choices.get(0);
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract content from AI response", e);
        }
        return generateFallbackContent();
    }
    
    private String generateFallbackContent() {
        return "🏔️ 今日份的徒步打卡完成！\n\n" +
               "和宝贝一起走过每一步，都是最美的风景 ✨\n\n" +
               "#亲子徒步 #户外运动 #快乐时光";
    }
}
