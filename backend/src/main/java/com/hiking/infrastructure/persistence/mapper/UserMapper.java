package com.hiking.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE open_id = #{openId}")
    User selectByOpenId(@Param("openId") String openId);

    @Select("SELECT COUNT(*) > 0 FROM user WHERE open_id = #{openId}")
    boolean existsByOpenId(@Param("openId") String openId);
    
    @Update("UPDATE user SET post_count = COALESCE(post_count, 0) + 1 WHERE id = #{userId}")
    void incrementPostCount(@Param("userId") Long userId);
    
    @Update("UPDATE user SET post_count = GREATEST(COALESCE(post_count, 0) - 1, 0) WHERE id = #{userId}")
    void decrementPostCount(@Param("userId") Long userId);
    
    @Update("UPDATE user SET fans_count = COALESCE(fans_count, 0) + 1 WHERE id = #{userId}")
    void incrementFansCount(@Param("userId") Long userId);
    
    @Update("UPDATE user SET fans_count = GREATEST(COALESCE(fans_count, 0) - 1, 0) WHERE id = #{userId}")
    void decrementFansCount(@Param("userId") Long userId);
    
    @Update("UPDATE user SET following_count = COALESCE(following_count, 0) + 1 WHERE id = #{userId}")
    void incrementFollowingCount(@Param("userId") Long userId);
    
    @Update("UPDATE user SET following_count = GREATEST(COALESCE(following_count, 0) - 1, 0) WHERE id = #{userId}")
    void decrementFollowingCount(@Param("userId") Long userId);
}
