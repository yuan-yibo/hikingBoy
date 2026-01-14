package com.hiking.infrastructure.persistence.interaction;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_like")
public class UserLikeDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetId;
    private Integer targetType;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
