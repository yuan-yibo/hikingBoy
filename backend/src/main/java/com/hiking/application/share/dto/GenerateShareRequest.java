package com.hiking.application.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 生成分享文案请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateShareRequest {
    
    /**
     * 徒步记录ID
     */
    private Long recordId;
    
    /**
     * 平台代码: xiaohongshu, moments, weibo
     */
    private String platform;
}
