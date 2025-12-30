package com.hiking.domain.team.repository;

import com.hiking.domain.team.entity.TeamMember;
import com.hiking.domain.team.valueobject.MemberStatus;

import java.util.List;
import java.util.Optional;

/**
 * 团队成员仓储接口
 */
public interface TeamMemberRepository {

    TeamMember save(TeamMember member);

    Optional<TeamMember> findById(Long id);

    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    List<TeamMember> findByTeamId(Long teamId);

    List<TeamMember> findByTeamIdAndStatus(Long teamId, MemberStatus status);

    List<TeamMember> findByUserId(Long userId);

    List<TeamMember> findApprovedByUserId(Long userId);

    void deleteById(Long id);

    void deleteByTeamId(Long teamId);

    boolean existsByTeamIdAndUserId(Long teamId, Long userId);

    long countApprovedByTeamId(Long teamId);
}
