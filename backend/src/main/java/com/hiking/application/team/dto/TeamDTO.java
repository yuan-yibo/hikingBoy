package com.hiking.application.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 团队响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private String inviteCode;
    private Long ownerId;
    private String ownerName;
    private Integer memberCount;
    private LocalDateTime createTime;
}
