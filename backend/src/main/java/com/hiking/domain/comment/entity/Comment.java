package com.hiking.domain.comment.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private Long replyToUserId;
    private String content;
    private Integer likeCount;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 扩展字段
    private String userName;
    private String userAvatar;
    private String replyToUserName;
    private Boolean isLiked;
    
    public static final int STATUS_DELETED = 0;
    public static final int STATUS_NORMAL = 1;
}
