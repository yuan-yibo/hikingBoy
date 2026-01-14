package com.hiking.domain.interaction.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 关注实体
 */
@Data
public class UserFollow {
    private Long id;
    private Long userId;
    private Long followUserId;
    private LocalDateTime createTime;
}
