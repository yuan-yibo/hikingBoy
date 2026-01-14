package com.hiking.infrastructure.persistence.comment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hiking.domain.comment.entity.Comment;
import com.hiking.domain.comment.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepository {
    
    private final CommentMapper commentMapper;
    
    @Override
    public Comment findById(Long id) {
        CommentDO commentDO = commentMapper.selectById(id);
        return commentDO != null ? toEntity(commentDO) : null;
    }
    
    @Override
    public List<Comment> findByPostId(Long postId, int page, int size) {
        return commentMapper.selectPage(new Page<>(page, size),
            new LambdaQueryWrapper<CommentDO>()
                .eq(CommentDO::getPostId, postId)
                .eq(CommentDO::getParentId, 0L)
                .eq(CommentDO::getStatus, Comment.STATUS_NORMAL)
                .orderByDesc(CommentDO::getCreateTime))
            .getRecords().stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public List<Comment> findReplies(Long parentId) {
        return commentMapper.selectList(new LambdaQueryWrapper<CommentDO>()
            .eq(CommentDO::getParentId, parentId)
            .eq(CommentDO::getStatus, Comment.STATUS_NORMAL)
            .orderByAsc(CommentDO::getCreateTime))
            .stream().map(this::toEntity).collect(Collectors.toList());
    }
    
    @Override
    public Long save(Comment comment) {
        CommentDO commentDO = new CommentDO();
        commentDO.setPostId(comment.getPostId());
        commentDO.setUserId(comment.getUserId());
        commentDO.setParentId(comment.getParentId() != null ? comment.getParentId() : 0L);
        commentDO.setReplyToUserId(comment.getReplyToUserId());
        commentDO.setContent(comment.getContent());
        commentDO.setLikeCount(0);
        commentDO.setStatus(Comment.STATUS_NORMAL);
        commentDO.setCreateTime(LocalDateTime.now());
        commentDO.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(commentDO);
        return commentDO.getId();
    }
    
    @Override
    public void delete(Long id) {
        CommentDO commentDO = new CommentDO();
        commentDO.setId(id);
        commentDO.setStatus(Comment.STATUS_DELETED);
        commentDO.setUpdateTime(LocalDateTime.now());
        commentMapper.updateById(commentDO);
    }
    
    @Override
    public void updateLikeCount(Long id, int delta) {
        commentMapper.updateLikeCount(id, delta);
    }
    
    private Comment toEntity(CommentDO commentDO) {
        Comment comment = new Comment();
        comment.setId(commentDO.getId());
        comment.setPostId(commentDO.getPostId());
        comment.setUserId(commentDO.getUserId());
        comment.setParentId(commentDO.getParentId());
        comment.setReplyToUserId(commentDO.getReplyToUserId());
        comment.setContent(commentDO.getContent());
        comment.setLikeCount(commentDO.getLikeCount());
        comment.setStatus(commentDO.getStatus());
        comment.setCreateTime(commentDO.getCreateTime());
        comment.setUpdateTime(commentDO.getUpdateTime());
        return comment;
    }
}
