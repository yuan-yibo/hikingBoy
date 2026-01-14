package com.hiking.infrastructure.persistence.post;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hiking.domain.post.entity.Post;
import com.hiking.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子仓储实现
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    
    private final PostMapper postMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public Post findById(Long id) {
        PostDO postDO = postMapper.selectById(id);
        return postDO != null ? toEntity(postDO) : null;
    }
    
    @Override
    public List<Post> findByUserId(Long userId, int page, int size) {
        Page<PostDO> pageResult = postMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<PostDO>()
                .eq(PostDO::getUserId, userId)
                .eq(PostDO::getStatus, Post.STATUS_NORMAL)
                .orderByDesc(PostDO::getCreateTime)
        );
        return pageResult.getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Post> findByTopic(String topic, int page, int size) {
        Page<PostDO> pageResult = postMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<PostDO>()
                .like(PostDO::getTopics, topic)
                .eq(PostDO::getStatus, Post.STATUS_NORMAL)
                .orderByDesc(PostDO::getCreateTime)
        );
        return pageResult.getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Post> findHotPosts(int page, int size) {
        Page<PostDO> pageResult = postMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<PostDO>()
                .eq(PostDO::getStatus, Post.STATUS_NORMAL)
                .orderByDesc(PostDO::getLikeCount)
                .orderByDesc(PostDO::getCreateTime)
        );
        return pageResult.getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Post> findLatestPosts(int page, int size) {
        Page<PostDO> pageResult = postMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<PostDO>()
                .eq(PostDO::getStatus, Post.STATUS_NORMAL)
                .orderByDesc(PostDO::getCreateTime)
        );
        return pageResult.getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Post> search(String keyword, int page, int size) {
        Page<PostDO> pageResult = postMapper.selectPage(
            new Page<>(page, size),
            new LambdaQueryWrapper<PostDO>()
                .eq(PostDO::getStatus, Post.STATUS_NORMAL)
                .and(w -> w.like(PostDO::getTitle, keyword)
                    .or().like(PostDO::getContent, keyword)
                    .or().like(PostDO::getLocation, keyword))
                .orderByDesc(PostDO::getCreateTime)
        );
        return pageResult.getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public Long save(Post post) {
        PostDO postDO = toDO(post);
        postDO.setCreateTime(LocalDateTime.now());
        postDO.setUpdateTime(LocalDateTime.now());
        postDO.setLikeCount(0);
        postDO.setCollectCount(0);
        postDO.setCommentCount(0);
        postDO.setViewCount(0);
        postDO.setStatus(Post.STATUS_NORMAL);
        postMapper.insert(postDO);
        return postDO.getId();
    }
    
    @Override
    public void update(Post post) {
        PostDO postDO = toDO(post);
        postDO.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(postDO);
    }
    
    @Override
    public void delete(Long id) {
        PostDO postDO = new PostDO();
        postDO.setId(id);
        postDO.setStatus(Post.STATUS_DELETED);
        postDO.setUpdateTime(LocalDateTime.now());
        postMapper.updateById(postDO);
    }
    
    @Override
    public void incrementViewCount(Long id) {
        postMapper.incrementViewCount(id);
    }
    
    @Override
    public void updateLikeCount(Long id, int delta) {
        postMapper.updateLikeCount(id, delta);
    }
    
    @Override
    public void updateCollectCount(Long id, int delta) {
        postMapper.updateCollectCount(id, delta);
    }
    
    @Override
    public void updateCommentCount(Long id, int delta) {
        postMapper.updateCommentCount(id, delta);
    }
    
    @SneakyThrows
    private Post toEntity(PostDO postDO) {
        Post post = new Post();
        post.setId(postDO.getId());
        post.setUserId(postDO.getUserId());
        post.setTitle(postDO.getTitle());
        post.setContent(postDO.getContent());
        post.setImages(parseJsonList(postDO.getImages()));
        post.setTopics(parseJsonList(postDO.getTopics()));
        post.setLocation(postDO.getLocation());
        post.setLatitude(postDO.getLatitude());
        post.setLongitude(postDO.getLongitude());
        post.setTrackId(postDO.getTrackId());
        post.setLikeCount(postDO.getLikeCount());
        post.setCollectCount(postDO.getCollectCount());
        post.setCommentCount(postDO.getCommentCount());
        post.setViewCount(postDO.getViewCount());
        post.setStatus(postDO.getStatus());
        post.setCreateTime(postDO.getCreateTime());
        post.setUpdateTime(postDO.getUpdateTime());
        return post;
    }
    
    @SneakyThrows
    private PostDO toDO(Post post) {
        PostDO postDO = new PostDO();
        postDO.setId(post.getId());
        postDO.setUserId(post.getUserId());
        postDO.setTitle(post.getTitle());
        postDO.setContent(post.getContent());
        postDO.setImages(toJson(post.getImages()));
        postDO.setTopics(toJson(post.getTopics()));
        postDO.setLocation(post.getLocation());
        postDO.setLatitude(post.getLatitude());
        postDO.setLongitude(post.getLongitude());
        postDO.setTrackId(post.getTrackId());
        postDO.setStatus(post.getStatus());
        return postDO;
    }
    
    @SneakyThrows
    private List<String> parseJsonList(String json) {
        if (json == null || json.isEmpty()) return Collections.emptyList();
        return objectMapper.readValue(json, new TypeReference<List<String>>() {});
    }
    
    @SneakyThrows
    private String toJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        return objectMapper.writeValueAsString(list);
    }
}
