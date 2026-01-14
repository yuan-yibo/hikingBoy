package com.hiking.application.post.dto;

import lombok.Data;

/**
 * 帖子列表查询请求
 */
@Data
public class PostQueryRequest {
    
    private String topic;
    
    private String keyword;
    
    private Long userId;
    
    private String sortBy = "latest"; // latest, hot
    
    private Integer page = 1;
    
    private Integer size = 10;
}
