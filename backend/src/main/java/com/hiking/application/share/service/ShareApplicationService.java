package com.hiking.application.share.service;

import com.hiking.application.share.dto.PlatformDTO;
import com.hiking.application.share.dto.ShareContentDTO;
import com.hiking.domain.hiking.entity.HikingRecord;
import com.hiking.domain.hiking.repository.HikingRecordRepository;
import com.hiking.domain.share.ShareStrategy;
import com.hiking.domain.share.ShareStrategyFactory;
import com.hiking.domain.share.SocialPlatform;
import com.hiking.infrastructure.ai.AiProperties;
import com.hiking.infrastructure.ai.AiService;
import com.hiking.infrastructure.ai.LocalContentGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分享应用服务 - 使用策略模式为不同平台生成文案
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShareApplicationService {
    
    private final ShareStrategyFactory strategyFactory;
    private final AiService aiService;
    private final AiProperties aiProperties;
    private final LocalContentGenerator localContentGenerator;
    private final HikingRecordRepository hikingRecordRepository;
    
    /**
     * 获取所有支持的平台
     */
    public List<PlatformDTO> getSupportedPlatforms() {
        return Arrays.stream(SocialPlatform.values())
                .map(platform -> PlatformDTO.builder()
                        .code(platform.getCode())
                        .name(platform.getDisplayName())
                        .icon(getIconForPlatform(platform))
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * 为指定平台生成分享文案
     */
    public ShareContentDTO generateShareContent(Long recordId, String platformCode) {
        // 1. 获取徒步记录
        HikingRecord record = hikingRecordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Record not found: " + recordId));
        
        // 2. 获取对应平台
        SocialPlatform platform = SocialPlatform.fromCode(platformCode);
        
        // 3. 生成文案（优先使用AI，否则使用本地生成）
        String processedContent = generateContentForPlatform(record, platform);
        
        // 4. 解析并返回结果
        return parseContent(platform, processedContent);
    }
    
    /**
     * 为平台生成文案
     */
    private String generateContentForPlatform(HikingRecord record, SocialPlatform platform) {
        // 检查AI是否可用（启用且有有效key）
        if (isAiAvailable()) {
            try {
                ShareStrategy strategy = strategyFactory.getStrategy(platform);
                String promptTemplate = aiService.loadPromptTemplate(platform.getPromptPath());
                String prompt = strategy.buildPrompt(record, promptTemplate);
                log.debug("Generated prompt for {}: {}", platform, prompt);
                
                String rawContent = aiService.generateContent(prompt);
                return strategy.postProcess(rawContent);
            } catch (Exception e) {
                log.warn("AI generation failed, falling back to local: {}", e.getMessage());
            }
        }
        
        // 使用本地智能生成
        log.info("Using local content generator for platform: {}", platform);
        return localContentGenerator.generate(platform, record);
    }
    
    /**
     * 检查AI服务是否可用
     */
    private boolean isAiAvailable() {
        if (!aiProperties.isEnabled()) {
            return false;
        }
        String apiKey = aiProperties.getApiKey();
        // 检查key是否有效（不是默认值且不为空）
        return apiKey != null 
                && !apiKey.isBlank() 
                && !apiKey.equals("sk-your-api-key-here")
                && !apiKey.startsWith("sk-your");
    }
    
    /**
     * 解析AI生成的内容
     */
    private ShareContentDTO parseContent(SocialPlatform platform, String content) {
        ShareContentDTO.ShareContentDTOBuilder builder = ShareContentDTO.builder()
                .platform(platform.getCode())
                .platformName(platform.getDisplayName())
                .content(content);
        
        // 小红书需要解析标题和正文
        if (platform == SocialPlatform.XIAOHONGSHU) {
            String title = "";
            String body = content;
            
            if (content.contains("标题：")) {
                int titleStart = content.indexOf("标题：") + 3;
                int titleEnd = content.indexOf("\n", titleStart);
                if (titleEnd == -1) titleEnd = content.length();
                title = content.substring(titleStart, titleEnd).trim();
            }
            
            if (content.contains("正文：")) {
                int bodyStart = content.indexOf("正文：") + 3;
                body = content.substring(bodyStart).trim();
            }
            
            builder.title(title).body(body);
        } else {
            builder.body(content);
        }
        
        return builder.build();
    }
    
    /**
     * 获取平台图标
     */
    private String getIconForPlatform(SocialPlatform platform) {
        return switch (platform) {
            case XIAOHONGSHU -> "/images/redbook.png";
            case MOMENTS -> "/images/circle.png";
            case WEIBO -> "/images/blog.png";
        };
    }
}
