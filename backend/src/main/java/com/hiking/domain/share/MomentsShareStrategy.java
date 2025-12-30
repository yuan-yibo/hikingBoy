package com.hiking.domain.share;

import com.hiking.domain.hiking.entity.HikingRecord;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * 朋友圈分享策略实现
 */
@Component
public class MomentsShareStrategy implements ShareStrategy {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("M月d日");
    
    @Override
    public SocialPlatform getPlatform() {
        return SocialPlatform.MOMENTS;
    }
    
    @Override
    public String buildPrompt(HikingRecord record, String promptTemplate) {
        String date = record.getHikingDate() != null 
            ? record.getHikingDate().format(DATE_FORMATTER) 
            : "今天";
        
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
        // 朋友圈文案简洁，去除多余空行
        return content.replaceAll("\n{3,}", "\n\n").trim();
    }
    
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
    
    private String formatDistance(Double distance) {
        if (distance == null) return "0";
        return String.format("%.1f", distance);
    }
}
