package com.hiking.domain.share;

/**
 * 社交媒体平台类型
 */
public enum SocialPlatform {
    
    XIAOHONGSHU("xiaohongshu", "小红书", "prompts/xiaohongshu.txt"),
    MOMENTS("moments", "朋友圈", "prompts/moments.txt"),
    WEIBO("weibo", "微博", "prompts/weibo.txt");
    
    private final String code;
    private final String displayName;
    private final String promptPath;
    
    SocialPlatform(String code, String displayName, String promptPath) {
        this.code = code;
        this.displayName = displayName;
        this.promptPath = promptPath;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getPromptPath() {
        return promptPath;
    }
    
    public static SocialPlatform fromCode(String code) {
        for (SocialPlatform platform : values()) {
            if (platform.code.equalsIgnoreCase(code)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("Unknown platform: " + code);
    }
}
