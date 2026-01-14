package com.hiking.domain.user.repository;

import com.hiking.domain.user.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口
 */
public interface UserRepository {

    User save(User user);

    User findById(Long id);

    Optional<User> findByOpenId(String openId);

    boolean existsByOpenId(String openId);
    
    void incrementPostCount(Long userId);
    
    void decrementPostCount(Long userId);
    
    void incrementFansCount(Long userId);
    
    void decrementFansCount(Long userId);
    
    void incrementFollowingCount(Long userId);
    
    void decrementFollowingCount(Long userId);
    
    List<User> findByIds(List<Long> ids);
}
