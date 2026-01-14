package com.hiking.interfaces.rest;

import com.hiking.application.post.dto.*;
import com.hiking.application.post.service.PostService;
import com.hiking.interfaces.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 帖子接口
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "帖子管理", description = "帖子相关接口")
public class PostController {
    
    private final PostService postService;
    
    @PostMapping
    @Operation(summary = "发布帖子")
    public ApiResponse<Long> createPost(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreatePostRequest request) {
        Long postId = postService.createPost(userId, request);
        return ApiResponse.success(postId);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取帖子详情")
    public ApiResponse<PostDTO> getPost(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        PostDTO post = postService.getPostDetail(id, userId);
        if (post == null) {
            return ApiResponse.error("帖子不存在");
        }
        return ApiResponse.success(post);
    }
    
    @GetMapping
    @Operation(summary = "获取帖子列表")
    public ApiResponse<List<PostDTO>> getPosts(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            PostQueryRequest request) {
        List<PostDTO> posts = postService.getPosts(request, userId);
        return ApiResponse.success(posts);
    }
    
    @GetMapping("/user/{targetUserId}")
    @Operation(summary = "获取用户的帖子")
    public ApiResponse<List<PostDTO>> getUserPosts(
            @PathVariable Long targetUserId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PostQueryRequest request = new PostQueryRequest();
        request.setUserId(targetUserId);
        request.setPage(page);
        request.setSize(size);
        List<PostDTO> posts = postService.getPosts(request, userId);
        return ApiResponse.success(posts);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除帖子")
    public ApiResponse<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        postService.deletePost(id, userId);
        return ApiResponse.success(null);
    }
}
