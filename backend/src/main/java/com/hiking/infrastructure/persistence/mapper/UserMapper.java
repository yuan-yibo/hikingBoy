package com.hiking.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE open_id = #{openId}")
    User selectByOpenId(@Param("openId") String openId);

    @Select("SELECT COUNT(*) > 0 FROM user WHERE open_id = #{openId}")
    boolean existsByOpenId(@Param("openId") String openId);
}
