package com.hiking.domain.share;

import com.hiking.domain.hiking.entity.HikingRecord;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * 微博分享策略实现
 */
@Component
public class WeiboShareStrategy implements ShareStrategy {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M.d");
    
    @Override
    public SocialPlatform getPlatform() {
        return SocialPlatform.WEIBO;
    }
    
    @Override
    public String buildPrompt(HikingRecord record, String promptTemplate) {
        String date = record.getHikingDate() != null 
            ? record.getHikingDate().format(DATE_FORMATTER) 
            : "今日";
        
        return promptTemplate
            .replace("{{mountainName}}", nullToEmpty(record.getMountainName()))
            .replace("{{date}}", date)
            .replace("{{distance}}", formatDistance(record.getDistance()))
            .replace("{{duration}}", nullToEmpty(record.getDuration()))
            .replace("{{weather}}", nullToEmpty(record.getWeatherType()))
            .replace("{{note}}", nullToEmpty(record.getNotes()));
    }
    
    @Override
    public String postProcess(String content) {
        // 微博特殊处理：确保话题标签格式正确
        // 将 #xxx 格式转为 #xxx# 格式（微博标准格式）
        return content.replaceAll("#([^#\\s]+)(?!#)", "#$1#");
    }
    
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
    
    private String formatDistance(Double distance) {
        if (distance == null) return "0";
        return String.format("%.1f", distance);
    }
}
