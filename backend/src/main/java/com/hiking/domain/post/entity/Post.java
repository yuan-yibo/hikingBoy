package com.hiking.domain.post.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 帖子实体
 */
@Data
public class Post {
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
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 关联信息（非持久化）
    private String authorName;
    private String authorAvatar;
    private Boolean isLiked;
    private Boolean isCollected;
    private Boolean isFollowed;
    
    public static final int STATUS_DELETED = 0;
    public static final int STATUS_NORMAL = 1;
    public static final int STATUS_PENDING = 2;
}
