package com.hiking.infrastructure.persistence.interaction;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_collection")
public class UserCollectionDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long postId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
