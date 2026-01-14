package com.hiking.application.comment.service;

import com.hiking.application.comment.dto.*;
import com.hiking.domain.comment.entity.Comment;
import com.hiking.domain.comment.repository.CommentRepository;
import com.hiking.domain.post.repository.PostRepository;
import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public Long createComment(Long userId, CreateCommentRequest request) {
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setParentId(request.getParentId());
        comment.setReplyToUserId(request.getReplyToUserId());
        comment.setContent(request.getContent());
        
        Long commentId = commentRepository.save(comment);
        postRepository.updateCommentCount(request.getPostId(), 1);
        
        return commentId;
    }
    
    public List<CommentDTO> getComments(Long postId, int page, int size, Long currentUserId) {
        List<Comment> comments = commentRepository.findByPostId(postId, page, size);
        return comments.stream().map(c -> {
            CommentDTO dto = toDTO(c);
            // 获取子评论
            List<Comment> replies = commentRepository.findReplies(c.getId());
            dto.setReplies(replies.stream().map(this::toDTO).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment != null && comment.getUserId().equals(userId)) {
            commentRepository.delete(commentId);
            postRepository.updateCommentCount(comment.getPostId(), -1);
        }
    }
    
    private CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPostId());
        dto.setUserId(comment.getUserId());
        dto.setParentId(comment.getParentId());
        dto.setReplyToUserId(comment.getReplyToUserId());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreateTime(comment.getCreateTime());
        dto.setTimeText(formatTime(comment.getCreateTime()));
        
        User user = userRepository.findById(comment.getUserId());
        if (user != null) {
            dto.setUserName(user.getNickname());
            dto.setUserAvatar(user.getAvatar());
        }
        
        if (comment.getReplyToUserId() != null) {
            User replyToUser = userRepository.findById(comment.getReplyToUserId());
            if (replyToUser != null) {
                dto.setReplyToUserName(replyToUser.getNickname());
            }
        }
        
        return dto;
    }
    
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
        return (days / 7) + "周前";
    }
}
