package com.hiking.application.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分享文案响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareContentDTO {
    
    /**
     * 平台代码
     */
    private String platform;
    
    /**
     * 平台名称
     */
    private String platformName;
    
    /**
     * 生成的文案内容
     */
    private String content;
    
    /**
     * 标题（小红书专用）
     */
    private String title;
    
    /**
     * 正文
     */
    private String body;
}
