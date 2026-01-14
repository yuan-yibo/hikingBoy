package com.hiking.application.comment.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private Long replyToUserId;
    private String content;
    private Integer likeCount;
    private LocalDateTime createTime;
    private String timeText;
    
    private String userName;
    private String userAvatar;
    private String replyToUserName;
    private Boolean isLiked;
    
    private List<CommentDTO> replies;
}
