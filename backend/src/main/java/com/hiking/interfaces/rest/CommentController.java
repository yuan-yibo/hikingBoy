package com.hiking.interfaces.rest;

import com.hiking.application.comment.dto.*;
import com.hiking.application.comment.service.CommentService;
import com.hiking.interfaces.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "评论管理", description = "评论相关接口")
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping
    @Operation(summary = "发表评论")
    public ApiResponse<Long> createComment(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateCommentRequest request) {
        Long commentId = commentService.createComment(userId, request);
        return ApiResponse.success(commentId);
    }
    
    @GetMapping("/post/{postId}")
    @Operation(summary = "获取帖子的评论列表")
    public ApiResponse<List<CommentDTO>> getComments(
            @PathVariable Long postId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        List<CommentDTO> comments = commentService.getComments(postId, page, size, userId);
        return ApiResponse.success(comments);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除评论")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        commentService.deleteComment(id, userId);
        return ApiResponse.success(null);
    }
}
