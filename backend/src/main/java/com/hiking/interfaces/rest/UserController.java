package com.hiking.interfaces.rest;

import com.hiking.application.user.dto.LoginRequest;
import com.hiking.application.user.dto.LoginResponse;
import com.hiking.application.user.dto.UserDTO;
import com.hiking.application.user.service.UserApplicationService;
import com.hiking.interfaces.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    /**
     * 登录/注册
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = userApplicationService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(@RequestHeader("X-User-Id") String visitorId) {
        UserDTO user = userApplicationService.getUserByOpenId(visitorId);
        return ApiResponse.success(user);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
        UserDTO user = userApplicationService.getUserById(id);
        return ApiResponse.success(user);
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public ApiResponse<UserDTO> updateUser(
            @RequestHeader("X-User-Id") String visitorId,
            @RequestBody UserDTO request) {
        UserDTO user = userApplicationService.updateUserByOpenId(visitorId, request.getNickname(), request.getAvatar());
        return ApiResponse.success(user);
    }
}
