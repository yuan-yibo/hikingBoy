package com.hiking.domain.team.repository;

import com.hiking.domain.team.entity.Team;

import java.util.List;
import java.util.Optional;

/**
 * 团队仓储接口
 */
public interface TeamRepository {

    Team save(Team team);

    Optional<Team> findById(Long id);

    Optional<Team> findByInviteCode(String inviteCode);

    List<Team> findByOwnerId(Long ownerId);

    void deleteById(Long id);

    boolean existsById(Long id);
}
