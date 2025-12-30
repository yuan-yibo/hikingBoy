package com.hiking.application.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队成员响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDTO {
    private Long id;
    private Long teamId;
    private Long userId;
    private String nickname;
    private String avatar;
    private String role;
    private String status;
    private LocalDateTime joinTime;
}
