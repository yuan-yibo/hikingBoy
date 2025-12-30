package com.hiking.infrastructure.persistence.repository;

import com.hiking.domain.team.entity.Team;
import com.hiking.domain.team.repository.TeamRepository;
import com.hiking.infrastructure.persistence.mapper.TeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 团队仓储实现
 */
@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamMapper teamMapper;

    @Override
    public Team save(Team team) {
        if (team.getId() == null) {
            teamMapper.insert(team);
        } else {
            teamMapper.updateById(team);
        }
        return team;
    }

    @Override
    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(teamMapper.selectById(id));
    }

    @Override
    public Optional<Team> findByInviteCode(String inviteCode) {
        return Optional.ofNullable(teamMapper.selectByInviteCode(inviteCode));
    }

    @Override
    public List<Team> findByOwnerId(Long ownerId) {
        return teamMapper.selectByOwnerId(ownerId);
    }

    @Override
    public void deleteById(Long id) {
        teamMapper.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return teamMapper.selectById(id) != null;
    }
}
