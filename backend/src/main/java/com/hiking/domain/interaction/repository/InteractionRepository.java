package com.hiking.domain.interaction.repository;

import com.hiking.domain.interaction.entity.UserLike;
import com.hiking.domain.interaction.entity.UserCollection;
import com.hiking.domain.interaction.entity.UserFollow;

import java.util.List;

/**
 * 互动仓储接口
 */
public interface InteractionRepository {
    
    // 点赞相关
    boolean isLiked(Long userId, Long targetId, int targetType);
    
    void addLike(Long userId, Long targetId, int targetType);
    
    void removeLike(Long userId, Long targetId, int targetType);
    
    List<Long> getLikedTargetIds(Long userId, int targetType, List<Long> targetIds);
    
    // 收藏相关
    boolean isCollected(Long userId, Long postId);
    
    void addCollection(Long userId, Long postId);
    
    void removeCollection(Long userId, Long postId);
    
    List<Long> getCollectedPostIds(Long userId, List<Long> postIds);
    
    List<Long> getUserCollectionPostIds(Long userId, int page, int size);
    
    // 关注相关
    boolean isFollowing(Long userId, Long followUserId);
    
    void addFollow(Long userId, Long followUserId);
    
    void removeFollow(Long userId, Long followUserId);
    
    List<Long> getFollowingUserIds(Long userId, int page, int size);
    
    List<Long> getFansUserIds(Long userId, int page, int size);
    
    List<Long> getFollowedUserIds(Long userId, List<Long> userIds);
}
