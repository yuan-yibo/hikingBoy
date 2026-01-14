package com.hiking.application.interaction.service;

import com.hiking.domain.interaction.entity.UserLike;
import com.hiking.domain.interaction.repository.InteractionRepository;
import com.hiking.domain.post.repository.PostRepository;
import com.hiking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 互动服务 - 使用Redis实现点赞/收藏/关注
 */
@Service
@RequiredArgsConstructor
public class InteractionService {
    
    private final InteractionRepository interactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    
    // Redis Key前缀
    private static final String POST_LIKE_KEY = "post:like:";
    private static final String POST_COLLECT_KEY = "post:collect:";
    private static final String COMMENT_LIKE_KEY = "comment:like:";
    private static final String USER_FOLLOWING_KEY = "user:following:";
    private static final String USER_FANS_KEY = "user:fans:";
    
    /**
     * 点赞/取消点赞帖子
     */
    @Transactional
    public boolean togglePostLike(Long userId, Long postId) {
        String key = POST_LIKE_KEY + postId;
        String userIdStr = userId.toString();
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userIdStr);
        if (Boolean.TRUE.equals(isMember)) {
            // 取消点赞
            redisTemplate.opsForSet().remove(key, userIdStr);
            postRepository.updateLikeCount(postId, -1);
            return false;
        } else {
            // 点赞
            redisTemplate.opsForSet().add(key, userIdStr);
            postRepository.updateLikeCount(postId, 1);
            return true;
        }
    }
    
    /**
     * 点赞/取消点赞评论
     */
    public boolean toggleCommentLike(Long userId, Long commentId) {
        String key = COMMENT_LIKE_KEY + commentId;
        String userIdStr = userId.toString();
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userIdStr);
        if (Boolean.TRUE.equals(isMember)) {
            redisTemplate.opsForSet().remove(key, userIdStr);
            return false;
        } else {
            redisTemplate.opsForSet().add(key, userIdStr);
            return true;
        }
    }
    
    /**
     * 收藏/取消收藏帖子
     */
    @Transactional
    public boolean toggleCollection(Long userId, Long postId) {
        String key = POST_COLLECT_KEY + postId;
        String userIdStr = userId.toString();
        
        Boolean isMember = redisTemplate.opsForSet().isMember(key, userIdStr);
        if (Boolean.TRUE.equals(isMember)) {
            redisTemplate.opsForSet().remove(key, userIdStr);
            postRepository.updateCollectCount(postId, -1);
            return false;
        } else {
            redisTemplate.opsForSet().add(key, userIdStr);
            postRepository.updateCollectCount(postId, 1);
            return true;
        }
    }
    
    /**
     * 关注/取消关注用户
     */
    @Transactional
    public boolean toggleFollow(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new IllegalArgumentException("不能关注自己");
        }
        
        String followingKey = USER_FOLLOWING_KEY + userId;
        String fansKey = USER_FANS_KEY + targetUserId;
        String userIdStr = userId.toString();
        String targetUserIdStr = targetUserId.toString();
        
        Boolean isFollowing = redisTemplate.opsForSet().isMember(followingKey, targetUserIdStr);
        if (Boolean.TRUE.equals(isFollowing)) {
            // 取消关注
            redisTemplate.opsForSet().remove(followingKey, targetUserIdStr);
            redisTemplate.opsForSet().remove(fansKey, userIdStr);
            userRepository.decrementFollowingCount(userId);
            userRepository.decrementFansCount(targetUserId);
            return false;
        } else {
            // 关注
            redisTemplate.opsForSet().add(followingKey, targetUserIdStr);
            redisTemplate.opsForSet().add(fansKey, userIdStr);
            userRepository.incrementFollowingCount(userId);
            userRepository.incrementFansCount(targetUserId);
            return true;
        }
    }
    
    /**
     * 检查是否点赞帖子
     */
    public boolean isPostLiked(Long userId, Long postId) {
        String key = POST_LIKE_KEY + postId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }
    
    /**
     * 检查是否点赞评论
     */
    public boolean isCommentLiked(Long userId, Long commentId) {
        String key = COMMENT_LIKE_KEY + commentId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }
    
    /**
     * 检查是否点赞（兼容旧接口）
     */
    public boolean isLiked(Long userId, Long targetId, int targetType) {
        if (targetType == UserLike.TARGET_TYPE_POST) {
            return isPostLiked(userId, targetId);
        } else if (targetType == UserLike.TARGET_TYPE_COMMENT) {
            return isCommentLiked(userId, targetId);
        }
        return false;
    }
    
    /**
     * 检查是否收藏
     */
    public boolean isCollected(Long userId, Long postId) {
        String key = POST_COLLECT_KEY + postId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId.toString()));
    }
    
    /**
     * 检查是否关注
     */
    public boolean isFollowing(Long userId, Long targetUserId) {
        String key = USER_FOLLOWING_KEY + userId;
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, targetUserId.toString()));
    }
    
    /**
     * 获取帖子点赞数
     */
    public Long getPostLikeCount(Long postId) {
        String key = POST_LIKE_KEY + postId;
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0L;
    }
    
    /**获取帖子收藏数
     */
    public Long getPostCollectCount(Long postId) {
        String key = POST_COLLECT_KEY + postId;
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0L;
    }
    
    /**
     * 获取用户关注数
     */
    public Long getFollowingCount(Long userId) {
        String key = USER_FOLLOWING_KEY + userId;
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0L;
    }
    
    /**
     * 获取用户粉丝数
     */
    public Long getFansCount(Long userId) {
        String key = USER_FANS_KEY + userId;
        Long size = redisTemplate.opsForSet().size(key);
        return size != null ? size : 0L;
    }
}
