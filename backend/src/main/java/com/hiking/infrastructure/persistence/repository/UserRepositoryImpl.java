package com.hiking.infrastructure.persistence.repository;

import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import com.hiking.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户仓储实现
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.updateById(user);
        }
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.selectById(id));
    }

    @Override
    public Optional<User> findByOpenId(String openId) {
        return Optional.ofNullable(userMapper.selectByOpenId(openId));
    }

    @Override
    public boolean existsByOpenId(String openId) {
        return userMapper.existsByOpenId(openId);
    }
}
