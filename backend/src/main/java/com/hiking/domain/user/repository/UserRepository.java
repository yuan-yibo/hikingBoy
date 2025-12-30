package com.hiking.domain.user.repository;

import com.hiking.domain.user.entity.User;

import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByOpenId(String openId);

    boolean existsByOpenId(String openId);
}
