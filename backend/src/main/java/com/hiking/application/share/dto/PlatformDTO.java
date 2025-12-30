package com.hiking.application.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 平台信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformDTO {
    
    /**
     * 平台代码
     */
    private String code;
    
    /**
     * 平台显示名称
     */
    private String name;
    
    /**
     * 平台图标
     */
    private String icon;
}
