package com.hiking.domain.comment.repository;

import com.hiking.domain.comment.entity.Comment;
import java.util.List;

public interface CommentRepository {
    Comment findById(Long id);
    List<Comment> findByPostId(Long postId, int page, int size);
    List<Comment> findReplies(Long parentId);
    Long save(Comment comment);
    void delete(Long id);
    void updateLikeCount(Long id, int delta);
}
