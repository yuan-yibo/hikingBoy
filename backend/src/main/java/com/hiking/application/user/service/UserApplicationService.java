package com.hiking.application.user.service;

import com.hiking.application.user.dto.LoginRequest;
import com.hiking.application.user.dto.LoginResponse;
import com.hiking.application.user.dto.UserDTO;
import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 用户应用服务
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    /**
     * 登录/注册
     */
    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 开发模式：直接使用传入的 openId
        String openId = request.getOpenId();
        if (openId == null || openId.isBlank()) {
            // TODO: 正式环境需要调用微信接口获取 openId
            throw new IllegalArgumentException("OpenID 不能为空");
        }

        User user = userRepository.findByOpenId(openId)
                .map(existingUser -> {
                    existingUser.updateProfile(request.getNickname(), request.getAvatar());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    User newUser = User.create(openId, request.getNickname(), request.getAvatar());
                    return userRepository.save(newUser);
                });

        // 生成简单的 token（实际项目应使用 JWT）
        String token = UUID.randomUUID().toString();

        return LoginResponse.builder()
                .userId(user.getId())
                .token(token)
                .user(toDTO(user))
                .build();
    }

    /**
     * 获取用户信息
     */
    public UserDTO getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    /**
     * 根据 OpenID 获取用户
     */
    public UserDTO getUserByOpenId(String openId) {
        return userRepository.findByOpenId(openId)
                .map(this::toDTO)
                .orElse(null);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserDTO updateUser(Long userId, String nickname, String avatar) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.updateProfile(nickname, avatar);
        userRepository.save(user);
        return toDTO(user);
    }

    /**
     * 根据 OpenID 更新用户信息
     */
    @Transactional
    public UserDTO updateUserByOpenId(String openId, String nickname, String avatar) {
        User user = userRepository.findByOpenId(openId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.updateProfile(nickname, avatar);
        userRepository.save(user);
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .openId(user.getOpenId())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }
}
