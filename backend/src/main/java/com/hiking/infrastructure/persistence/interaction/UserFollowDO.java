package com.hiking.infrastructure.persistence.interaction;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_follow")
public class UserFollowDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long followUserId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
