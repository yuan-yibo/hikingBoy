package com.hiking.application.post.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子DTO
 */
@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private List<String> images;
    private List<String> topics;
    private String location;
    private Double latitude;
    private Double longitude;
    private Long trackId;
    private Integer likeCount;
    private Integer collectCount;
    private Integer commentCount;
    private Integer viewCount;
    private LocalDateTime createTime;
    
    // 作者信息
    private String authorName;
    private String authorAvatar;
    
    // 当前用户状态
    private Boolean isLiked;
    private Boolean isCollected;
    private Boolean isFollowed;
    
    // 格式化时间
    private String timeText;
}
