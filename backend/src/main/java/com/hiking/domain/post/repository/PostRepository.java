package com.hiking.domain.post.repository;

import com.hiking.domain.post.entity.Post;
import java.util.List;

/**
 * 帖子仓储接口
 */
public interface PostRepository {
    
    Post findById(Long id);
    
    List<Post> findByUserId(Long userId, int page, int size);
    
    List<Post> findByTopic(String topic, int page, int size);
    
    List<Post> findHotPosts(int page, int size);
    
    List<Post> findLatestPosts(int page, int size);
    
    List<Post> search(String keyword, int page, int size);
    
    Long save(Post post);
    
    void update(Post post);
    
    void delete(Long id);
    
    void incrementViewCount(Long id);
    
    void updateLikeCount(Long id, int delta);
    
    void updateCollectCount(Long id, int delta);
    
    void updateCommentCount(Long id, int delta);
}
