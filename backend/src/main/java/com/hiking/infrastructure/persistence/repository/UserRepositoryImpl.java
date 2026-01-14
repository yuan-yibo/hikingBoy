package com.hiking.infrastructure.persistence.repository;

import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import com.hiking.infrastructure.persistence.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
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
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public Optional<User> findByOpenId(String openId) {
        return Optional.ofNullable(userMapper.selectByOpenId(openId));
    }

    @Override
    public boolean existsByOpenId(String openId) {
        return userMapper.existsByOpenId(openId);
    }
    
    @Override
    public void incrementPostCount(Long userId) {
        userMapper.incrementPostCount(userId);
    }
    
    @Override
    public void decrementPostCount(Long userId) {
        userMapper.decrementPostCount(userId);
    }
    
    @Override
    public void incrementFansCount(Long userId) {
        userMapper.incrementFansCount(userId);
    }
    
    @Override
    public void decrementFansCount(Long userId) {
        userMapper.decrementFansCount(userId);
    }
    
    @Override
    public void incrementFollowingCount(Long userId) {
        userMapper.incrementFollowingCount(userId);
    }
    
    @Override
    public void decrementFollowingCount(Long userId) {
        userMapper.decrementFollowingCount(userId);
    }
    
    @Override
    public List<User> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        return userMapper.selectBatchIds(ids);
    }
}
