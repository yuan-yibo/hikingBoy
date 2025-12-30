package com.hiking.application.team.dto;

import lombok.Data;

/**
 * 创建团队请求DTO
 */
@Data
public class CreateTeamRequest {
    private String name;
    private String description;
}
