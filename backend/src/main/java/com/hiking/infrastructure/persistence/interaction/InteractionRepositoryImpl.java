package com.hiking.infrastructure.persistence.interaction;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hiking.domain.interaction.repository.InteractionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InteractionRepositoryImpl implements InteractionRepository {
    
    private final UserLikeMapper likeMapper;
    private final UserCollectionMapper collectionMapper;
    private final UserFollowMapper followMapper;
    
    // ===== 点赞 =====
    @Override
    public boolean isLiked(Long userId, Long targetId, int targetType) {
        return likeMapper.selectCount(new LambdaQueryWrapper<UserLikeDO>()
            .eq(UserLikeDO::getUserId, userId)
            .eq(UserLikeDO::getTargetId, targetId)
            .eq(UserLikeDO::getTargetType, targetType)) > 0;
    }
    
    @Override
    public void addLike(Long userId, Long targetId, int targetType) {
        UserLikeDO like = new UserLikeDO();
        like.setUserId(userId);
        like.setTargetId(targetId);
        like.setTargetType(targetType);
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);
    }
    
    @Override
    public void removeLike(Long userId, Long targetId, int targetType) {
        likeMapper.delete(new LambdaQueryWrapper<UserLikeDO>()
            .eq(UserLikeDO::getUserId, userId)
            .eq(UserLikeDO::getTargetId, targetId)
            .eq(UserLikeDO::getTargetType, targetType));
    }
    
    @Override
    public List<Long> getLikedTargetIds(Long userId, int targetType, List<Long> targetIds) {
        if (targetIds == null || targetIds.isEmpty()) return Collections.emptyList();
        return likeMapper.selectList(new LambdaQueryWrapper<UserLikeDO>()
            .eq(UserLikeDO::getUserId, userId)
            .eq(UserLikeDO::getTargetType, targetType)
            .in(UserLikeDO::getTargetId, targetIds))
            .stream().map(UserLikeDO::getTargetId).collect(Collectors.toList());
    }
    
    // ===== 收藏 =====
    @Override
    public boolean isCollected(Long userId, Long postId) {
        return collectionMapper.selectCount(new LambdaQueryWrapper<UserCollectionDO>()
            .eq(UserCollectionDO::getUserId, userId)
            .eq(UserCollectionDO::getPostId, postId)) > 0;
    }
    
    @Override
    public void addCollection(Long userId, Long postId) {
        UserCollectionDO collection = new UserCollectionDO();
        collection.setUserId(userId);
        collection.setPostId(postId);
        collection.setCreateTime(LocalDateTime.now());
        collectionMapper.insert(collection);
    }
    
    @Override
    public void removeCollection(Long userId, Long postId) {
        collectionMapper.delete(new LambdaQueryWrapper<UserCollectionDO>()
            .eq(UserCollectionDO::getUserId, userId)
            .eq(UserCollectionDO::getPostId, postId));
    }
    
    @Override
    public List<Long> getCollectedPostIds(Long userId, List<Long> postIds) {
        if (postIds == null || postIds.isEmpty()) return Collections.emptyList();
        return collectionMapper.selectList(new LambdaQueryWrapper<UserCollectionDO>()
            .eq(UserCollectionDO::getUserId, userId)
            .in(UserCollectionDO::getPostId, postIds))
            .stream().map(UserCollectionDO::getPostId).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getUserCollectionPostIds(Long userId, int page, int size) {
        return collectionMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<UserCollectionDO>()
                .eq(UserCollectionDO::getUserId, userId)
                .orderByDesc(UserCollectionDO::getCreateTime))
            .getRecords().stream().map(UserCollectionDO::getPostId).collect(Collectors.toList());
    }
    
    // ===== 关注 =====
    @Override
    public boolean isFollowing(Long userId, Long followUserId) {
        return followMapper.selectCount(new LambdaQueryWrapper<UserFollowDO>()
            .eq(UserFollowDO::getUserId, userId)
            .eq(UserFollowDO::getFollowUserId, followUserId)) > 0;
    }
    
    @Override
    public void addFollow(Long userId, Long followUserId) {
        UserFollowDO follow = new UserFollowDO();
        follow.setUserId(userId);
        follow.setFollowUserId(followUserId);
        follow.setCreateTime(LocalDateTime.now());
        followMapper.insert(follow);
    }
    
    @Override
    public void removeFollow(Long userId, Long followUserId) {
        followMapper.delete(new LambdaQueryWrapper<UserFollowDO>()
            .eq(UserFollowDO::getUserId, userId)
            .eq(UserFollowDO::getFollowUserId, followUserId));
    }
    
    @Override
    public List<Long> getFollowingUserIds(Long userId, int page, int size) {
        return followMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<UserFollowDO>()
                .eq(UserFollowDO::getUserId, userId)
                .orderByDesc(UserFollowDO::getCreateTime))
            .getRecords().stream().map(UserFollowDO::getFollowUserId).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getFansUserIds(Long userId, int page, int size) {
        return followMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<UserFollowDO>()
                .eq(UserFollowDO::getFollowUserId, userId)
                .orderByDesc(UserFollowDO::getCreateTime))
            .getRecords().stream().map(UserFollowDO::getUserId).collect(Collectors.toList());
    }
    
    @Override
    public List<Long> getFollowedUserIds(Long userId, List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) return Collections.emptyList();
        return followMapper.selectList(new LambdaQueryWrapper<UserFollowDO>()
            .eq(UserFollowDO::getUserId, userId)
            .in(UserFollowDO::getFollowUserId, userIds))
            .stream().map(UserFollowDO::getFollowUserId).collect(Collectors.toList());
    }
}
