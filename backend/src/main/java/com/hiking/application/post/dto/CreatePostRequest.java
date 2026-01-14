package com.hiking.application.post.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

/**
 * 创建帖子请求
 */
@Data
public class CreatePostRequest {
    
    private String title;
    
    private String content;
    
    @NotEmpty(message = "请至少上传一张图片")
    private List<String> images;
    
    private List<String> topics;
    
    private String location;
    
    private Double latitude;
    
    private Double longitude;
    
    private Long trackId;
}
