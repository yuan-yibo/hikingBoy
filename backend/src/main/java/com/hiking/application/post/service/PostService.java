package com.hiking.application.post.service;

import com.hiking.application.post.dto.*;
import com.hiking.domain.post.entity.Post;
import com.hiking.domain.post.repository.PostRepository;
import com.hiking.domain.user.repository.UserRepository;
import com.hiking.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子应用服务
 */
@Service
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    /**
     * 创建帖子
     */
    @Transactional
    public Long createPost(Long userId, CreatePostRequest request) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setImages(request.getImages());
        post.setTopics(request.getTopics());
        post.setLocation(request.getLocation());
        post.setLatitude(request.getLatitude());
        post.setLongitude(request.getLongitude());
        post.setTrackId(request.getTrackId());
        
        Long postId = postRepository.save(post);
        
        // 更新用户帖子数
        userRepository.incrementPostCount(userId);
        
        return postId;
    }
    
    /**
     * 获取帖子详情
     */
    public PostDTO getPostDetail(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId);
        if (post == null || post.getStatus() != Post.STATUS_NORMAL) {
            return null;
        }
        
        // 增加浏览量
        postRepository.incrementViewCount(postId);
        
        return toDTO(post, currentUserId);
    }
    
    /**
     * 获取帖子列表
     */
    public List<PostDTO> getPosts(PostQueryRequest request, Long currentUserId) {
        List<Post> posts;
        int page = request.getPage();
        int size = request.getSize();
        
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            posts = postRepository.search(request.getKeyword(), page, size);
        } else if (request.getUserId() != null) {
            posts = postRepository.findByUserId(request.getUserId(), page, size);
        } else if (request.getTopic() != null && !request.getTopic().isEmpty() && !"all".equals(request.getTopic())) {
            posts = postRepository.findByTopic(request.getTopic(), page, size);
        } else if ("hot".equals(request.getSortBy())) {
            posts = postRepository.findHotPosts(page, size);
        } else {
            posts = postRepository.findLatestPosts(page, size);
        }
        
        return posts.stream()
            .map(post -> toDTO(post, currentUserId))
            .collect(Collectors.toList());
    }
    
    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId);
        if (post != null && post.getUserId().equals(userId)) {
            postRepository.delete(postId);
            userRepository.decrementPostCount(userId);
        }
    }
    
    /**
     * 转换为DTO
     */
    private PostDTO toDTO(Post post, Long currentUserId) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUserId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImages(post.getImages());
        dto.setTopics(post.getTopics());
        dto.setLocation(post.getLocation());
        dto.setLatitude(post.getLatitude());
        dto.setLongitude(post.getLongitude());
        dto.setTrackId(post.getTrackId());
        dto.setLikeCount(post.getLikeCount());
        dto.setCollectCount(post.getCollectCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setViewCount(post.getViewCount());
        dto.setCreateTime(post.getCreateTime());
        dto.setTimeText(formatTime(post.getCreateTime()));
        
        // 获取作者信息
        User author = userRepository.findById(post.getUserId());
        if (author != null) {
            dto.setAuthorName(author.getNickname());
            dto.setAuthorAvatar(author.getAvatar());
        }
        
        // TODO: 查询当前用户的点赞、收藏、关注状态
        dto.setIsLiked(false);
        dto.setIsCollected(false);
        dto.setIsFollowed(false);
        
        return dto;
    }
    
    /**
     * 格式化时间
     */
    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        
        Duration duration = Duration.between(time, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();
        
        if (minutes < 1) return "刚刚";
        if (minutes < 60) return minutes + "分钟前";
        if (hours < 24) return hours + "小时前";
        if (days < 7) return days + "天前";
        if (days < 30) return (days / 7) + "周前";
        if (days < 365) return (days / 30) + "个月前";
        return (days / 365) + "年前";
    }
}
