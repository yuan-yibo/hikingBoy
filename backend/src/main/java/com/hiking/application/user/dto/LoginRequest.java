package com.hiking.application.user.dto;

import lombok.Data;

/**
 * 用户登录请求DTO
 */
@Data
public class LoginRequest {
    /**
     * 微信登录code
     */
    private String code;
    
    /**
     * 微信 OpenID（用于开发模式，直接传入）
     */
    private String openId;
    
    /**
     * 用户昵称
     */
    private String nickname;
    
    /**
     * 用户头像
     */
    private String avatar;
}
