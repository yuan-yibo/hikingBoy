package com.hiking.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.domain.team.entity.Team;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团队 Mapper
 */
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

    @Select("SELECT * FROM team WHERE invite_code = #{inviteCode}")
    Team selectByInviteCode(@Param("inviteCode") String inviteCode);

    @Select("SELECT * FROM team WHERE owner_id = #{ownerId} ORDER BY create_time DESC")
    List<Team> selectByOwnerId(@Param("ownerId") Long ownerId);
}
