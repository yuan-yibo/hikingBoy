package com.hiking.domain.interaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 点赞实体
 */
@Data
public class UserLike {
    private Long id;
    private Long userId;
    private Long targetId;
    private Integer targetType; // 1-帖子 2-评论
    private LocalDateTime createTime;
    
    public static final int TARGET_TYPE_POST = 1;
    public static final int TARGET_TYPE_COMMENT = 2;
}
