package com.hiking.domain.share;

import com.hiking.domain.hiking.entity.HikingRecord;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * 小红书分享策略实现
 */
@Component
public class XiaohongshuShareStrategy implements ShareStrategy {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年M月d日");
    
    @Override
    public SocialPlatform getPlatform() {
        return SocialPlatform.XIAOHONGSHU;
    }
    
    @Override
    public String buildPrompt(HikingRecord record, String promptTemplate) {
        String date = record.getHikingDate() != null 
            ? record.getHikingDate().format(DATE_FORMATTER) 
            : "未知日期";
        
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
        // 小红书特殊处理：确保有标题和正文格式
        if (!content.contains("标题：") && !content.contains("正文：")) {
            // 如果AI没有按格式输出，尝试提取第一行作为标题
            String[] lines = content.split("\n", 2);
            if (lines.length >= 2) {
                return "标题：" + lines[0].trim() + "\n正文：" + lines[1].trim();
            }
        }
        return content;
    }
    
    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
    
    private String formatDistance(Double distance) {
        if (distance == null) return "0";
        return String.format("%.1f", distance);
    }
}
