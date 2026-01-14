package com.hiking.infrastructure.persistence.comment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

@Mapper
public interface CommentMapper extends BaseMapper<CommentDO> {
    @Update("UPDATE comment SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
}
