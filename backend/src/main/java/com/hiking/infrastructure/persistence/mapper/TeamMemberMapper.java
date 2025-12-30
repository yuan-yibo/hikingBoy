package com.hiking.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.domain.team.entity.TeamMember;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 团队成员 Mapper
 */
@Mapper
public interface TeamMemberMapper extends BaseMapper<TeamMember> {

    @Select("SELECT * FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    TeamMember selectByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT * FROM team_member WHERE team_id = #{teamId} ORDER BY create_time")
    List<TeamMember> selectByTeamId(@Param("teamId") Long teamId);

    @Select("SELECT * FROM team_member WHERE team_id = #{teamId} AND status = #{status} ORDER BY create_time")
    List<TeamMember> selectByTeamIdAndStatus(@Param("teamId") Long teamId, @Param("status") String status);

    @Select("SELECT * FROM team_member WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<TeamMember> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM team_member WHERE user_id = #{userId} AND status = 'APPROVED' ORDER BY create_time DESC")
    List<TeamMember> selectApprovedByUserId(@Param("userId") Long userId);

    @Delete("DELETE FROM team_member WHERE team_id = #{teamId}")
    void deleteByTeamId(@Param("teamId") Long teamId);

    @Select("SELECT COUNT(*) > 0 FROM team_member WHERE team_id = #{teamId} AND user_id = #{userId}")
    boolean existsByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM team_member WHERE team_id = #{teamId} AND status = 'APPROVED'")
    long countApprovedByTeamId(@Param("teamId") Long teamId);
}
