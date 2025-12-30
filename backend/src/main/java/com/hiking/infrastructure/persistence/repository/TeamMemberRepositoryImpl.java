package com.hiking.infrastructure.persistence.repository;

import com.hiking.domain.team.entity.TeamMember;
import com.hiking.domain.team.repository.TeamMemberRepository;
import com.hiking.domain.team.valueobject.MemberStatus;
import com.hiking.infrastructure.persistence.mapper.TeamMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 团队成员仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TeamMemberRepositoryImpl implements TeamMemberRepository {

    private final TeamMemberMapper teamMemberMapper;

    @Override
    public TeamMember save(TeamMember member) {
        if (member.getId() == null) {
            teamMemberMapper.insert(member);
        } else {
            teamMemberMapper.updateById(member);
        }
        return member;
    }

    @Override
    public Optional<TeamMember> findById(Long id) {
        return Optional.ofNullable(teamMemberMapper.selectById(id));
    }

    @Override
    public Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId) {
        return Optional.ofNullable(teamMemberMapper.selectByTeamIdAndUserId(teamId, userId));
    }

    @Override
    public List<TeamMember> findByTeamId(Long teamId) {
        return teamMemberMapper.selectByTeamId(teamId);
    }

    @Override
    public List<TeamMember> findByTeamIdAndStatus(Long teamId, MemberStatus status) {
        return teamMemberMapper.selectByTeamIdAndStatus(teamId, status.name());
    }

    @Override
    public List<TeamMember> findByUserId(Long userId) {
        return teamMemberMapper.selectByUserId(userId);
    }

    @Override
    public List<TeamMember> findApprovedByUserId(Long userId) {
        return teamMemberMapper.selectApprovedByUserId(userId);
    }

    @Override
    public void deleteById(Long id) {
        teamMemberMapper.deleteById(id);
    }

    @Override
    public void deleteByTeamId(Long teamId) {
        teamMemberMapper.deleteByTeamId(teamId);
    }

    @Override
    public boolean existsByTeamIdAndUserId(Long teamId, Long userId) {
        return teamMemberMapper.existsByTeamIdAndUserId(teamId, userId);
    }

    @Override
    public long countApprovedByTeamId(Long teamId) {
        return teamMemberMapper.countApprovedByTeamId(teamId);
    }
}
