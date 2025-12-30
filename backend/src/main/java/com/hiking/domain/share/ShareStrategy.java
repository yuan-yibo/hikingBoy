package com.hiking.domain.share;

import com.hiking.domain.hiking.entity.HikingRecord;

/**
 * 分享策略接口 - 策略模式核心接口
 */
public interface ShareStrategy {
    
    /**
     * 获取平台类型
     */
    SocialPlatform getPlatform();
    
    /**
     * 构建发送给AI的prompt
     * @param record 徒步记录
     * @param promptTemplate prompt模板
     * @return 完整的prompt
     */
    String buildPrompt(HikingRecord record, String promptTemplate);
    
    /**
     * 后处理AI生成的文案（可选的额外处理）
     * @param content AI生成的原始内容
     * @return 处理后的内容
     */
    default String postProcess(String content) {
        return content;
    }
}
