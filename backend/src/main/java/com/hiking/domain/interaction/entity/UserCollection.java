package com.hiking.domain.interaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 收藏实体
 */
@Data
public class UserCollection {
    private Long id;
    private Long userId;
    private Long postId;
    private LocalDateTime createTime;
}
