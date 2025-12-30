package com.hiking.application.team.dto;

import lombok.Data;

/**
 * 更新团队请求DTO
 */
@Data
public class UpdateTeamRequest {
    private String name;
    private String description;
}
