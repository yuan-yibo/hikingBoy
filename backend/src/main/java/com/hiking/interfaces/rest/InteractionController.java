package com.hiking.interfaces.rest;

import com.hiking.application.interaction.service.InteractionService;
import com.hiking.interfaces.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 互动接口（点赞、收藏、关注）
 */
@RestController
@RequestMapping("/api/interactions")
@RequiredArgsConstructor
@Tag(name = "互动管理", description = "点赞、收藏、关注相关接口")
public class InteractionController {
    
    private final InteractionService interactionService;
    
    @PostMapping("/posts/{postId}/like")
    @Operation(summary = "点赞/取消点赞帖子")
    public ApiResponse<Map<String, Boolean>> togglePostLike(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isLiked = interactionService.togglePostLike(userId, postId);
        return ApiResponse.success(Map.of("isLiked", isLiked));
    }
    
    @PostMapping("/comments/{commentId}/like")
    @Operation(summary = "点赞/取消点赞评论")
    public ApiResponse<Map<String, Boolean>> toggleCommentLike(
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isLiked = interactionService.toggleCommentLike(userId, commentId);
        return ApiResponse.success(Map.of("isLiked", isLiked));
    }
    
    @PostMapping("/posts/{postId}/collect")
    @Operation(summary = "收藏/取消收藏帖子")
    public ApiResponse<Map<String, Boolean>> toggleCollection(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isCollected = interactionService.toggleCollection(userId, postId);
        return ApiResponse.success(Map.of("isCollected", isCollected));
    }
    
    @PostMapping("/users/{targetUserId}/follow")
    @Operation(summary = "关注/取消关注用户")
    public ApiResponse<Map<String, Boolean>> toggleFollow(
            @PathVariable Long targetUserId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            boolean isFollowing = interactionService.toggleFollow(userId, targetUserId);
            return ApiResponse.success(Map.of("isFollowing", isFollowing));
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
    
    @GetMapping("/posts/{postId}/like/status")
    @Operation(summary = "获取帖子点赞状态")
    public ApiResponse<Map<String, Boolean>> getPostLikeStatus(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isLiked = interactionService.isLiked(userId, postId, 1);
        return ApiResponse.success(Map.of("isLiked", isLiked));
    }
    
    @GetMapping("/posts/{postId}/collect/status")
    @Operation(summary = "获取帖子收藏状态")
    public ApiResponse<Map<String, Boolean>> getCollectStatus(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isCollected = interactionService.isCollected(userId, postId);
        return ApiResponse.success(Map.of("isCollected", isCollected));
    }
    
    @GetMapping("/users/{targetUserId}/follow/status")
    @Operation(summary = "获取关注状态")
    public ApiResponse<Map<String, Boolean>> getFollowStatus(
            @PathVariable Long targetUserId,
            @RequestHeader("X-User-Id") Long userId) {
        boolean isFollowing = interactionService.isFollowing(userId, targetUserId);
        return ApiResponse.success(Map.of("isFollowing", isFollowing));
    }
}
