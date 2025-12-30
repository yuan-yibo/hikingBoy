package com.hiking.application.team.dto;

import lombok.Data;

/**
 * 加入团队请求DTO
 */
@Data
public class JoinTeamRequest {
    /**
     * 邀请码
     */
    private String inviteCode;
}
