package com.hiking.domain.team.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 团队实体（聚合根）
 */
@TableName(value = "team", autoResultMap = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Team {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 团队名称
     */
    @TableField("name")
    private String name;

    /**
     * 团队描述
     */
    @TableField("description")
    private String description;

    /**
     * 邀请码（用于加入团队）
     */
    @TableField("invite_code")
    private String inviteCode;

    /**
     * 团队创建者ID
     */
    @TableField("owner_id")
    private Long ownerId;

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
     * 创建团队
     */
    public static Team create(String name, String description, Long ownerId) {
        Team team = new Team();
        team.name = name;
        team.description = description;
        team.ownerId = ownerId;
        team.inviteCode = generateInviteCode();
        team.createTime = LocalDateTime.now();
        team.updateTime = LocalDateTime.now();
        
        team.validate();
        return team;
    }

    /**
     * 更新团队信息
     */
    public void update(String name, String description) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (description != null) {
            this.description = description;
        }
        this.updateTime = LocalDateTime.now();
        validate();
    }

    /**
     * 重新生成邀请码
     */
    public void regenerateInviteCode() {
        this.inviteCode = generateInviteCode();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 验证是否为团队所有者
     */
    public boolean isOwner(Long userId) {
        return this.ownerId != null && this.ownerId.equals(userId);
    }

    /**
     * 验证业务规则
     */
    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("团队名称不能为空");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("团队名称不能超过50个字符");
        }
    }

    /**
     * 生成邀请码
     */
    private static String generateInviteCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
