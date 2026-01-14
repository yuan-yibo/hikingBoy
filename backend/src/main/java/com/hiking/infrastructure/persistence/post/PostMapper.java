package com.hiking.infrastructure.persistence.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 帖子Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<PostDO> {
    
    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    void incrementViewCount(@Param("id") Long id);
    
    @Update("UPDATE post SET like_count = like_count + #{delta} WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);
    
    @Update("UPDATE post SET collect_count = collect_count + #{delta} WHERE id = #{id}")
    void updateCollectCount(@Param("id") Long id, @Param("delta") int delta);
    
    @Update("UPDATE post SET comment_count = comment_count + #{delta} WHERE id = #{id}")
    void updateCommentCount(@Param("id") Long id, @Param("delta") int delta);
}
