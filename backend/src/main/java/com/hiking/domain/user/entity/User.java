package com.hiking.domain.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@TableName(value = "user", autoResultMap = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 微信 OpenID
     */
    @TableField("open_id")
    private String openId;

    /**
     * 用户昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户头像
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建用户
     */
    public static User create(String openId, String nickname, String avatar) {
        User user = new User();
        user.openId = openId;
        user.nickname = nickname != null ? nickname : "徒步爱好者";
        user.avatar = avatar;
        user.createTime = LocalDateTime.now();
        user.updateTime = LocalDateTime.now();
        return user;
    }

    /**
     * 更新用户信息
     */
    public void updateProfile(String nickname, String avatar) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        if (avatar != null && !avatar.isBlank()) {
            this.avatar = avatar;
        }
        this.updateTime = LocalDateTime.now();
    }
}
