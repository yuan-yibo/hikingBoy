package com.hiking.domain.team.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hiking.domain.team.valueobject.MemberRole;
import com.hiking.domain.team.valueobject.MemberStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队成员实体
 */
@TableName(value = "team_member", autoResultMap = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 团队ID
     */
    @TableField("team_id")
    private Long teamId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 成员角色
     */
    @TableField("role")
    private MemberRole role;

    /**
     * 成员状态
     */
    @TableField("status")
    private MemberStatus status;

    /**
     * 加入时间
     */
    @TableField("join_time")
    private LocalDateTime joinTime;

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
     * 创建团队所有者成员
     */
    public static TeamMember createOwner(Long teamId, Long userId) {
        TeamMember member = new TeamMember();
        member.teamId = teamId;
        member.userId = userId;
        member.role = MemberRole.OWNER;
        member.status = MemberStatus.APPROVED;
        member.joinTime = LocalDateTime.now();
        member.createTime = LocalDateTime.now();
        member.updateTime = LocalDateTime.now();
        return member;
    }

    /**
     * 创建申请加入的成员
     */
    public static TeamMember createApplicant(Long teamId, Long userId) {
        TeamMember member = new TeamMember();
        member.teamId = teamId;
        member.userId = userId;
        member.role = MemberRole.MEMBER;
        member.status = MemberStatus.PENDING;
        member.createTime = LocalDateTime.now();
        member.updateTime = LocalDateTime.now();
        return member;
    }

    /**
     * 通过邀请码加入（直接批准）
     */
    public static TeamMember createByInviteCode(Long teamId, Long userId) {
        TeamMember member = new TeamMember();
        member.teamId = teamId;
        member.userId = userId;
        member.role = MemberRole.MEMBER;
        member.status = MemberStatus.APPROVED;
        member.joinTime = LocalDateTime.now();
        member.createTime = LocalDateTime.now();
        member.updateTime = LocalDateTime.now();
        return member;
    }

    /**
     * 批准加入
     */
    public void approve() {
        if (this.status != MemberStatus.PENDING) {
            throw new IllegalStateException("只能审批待处理的申请");
        }
        this.status = MemberStatus.APPROVED;
        this.joinTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 拒绝加入
     */
    public void reject() {
        if (this.status != MemberStatus.PENDING) {
            throw new IllegalStateException("只能审批待处理的申请");
        }
        this.status = MemberStatus.REJECTED;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 是否已批准
     */
    public boolean isApproved() {
        return this.status == MemberStatus.APPROVED;
    }

    /**
     * 是否为所有者
     */
    public boolean isOwner() {
        return this.role == MemberRole.OWNER;
    }
}
