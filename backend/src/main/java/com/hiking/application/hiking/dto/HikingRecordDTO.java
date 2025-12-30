package com.hiking.application.hiking.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 徒步记录数据传输对象
 */
@Data
public class HikingRecordDTO {

    private Long id;

    private String userId;

    private Long ownerId;

    private Long teamId;

    private String ownerName;

    private String mountainName;

    private LocalDate hikingDate;

    private Double distance;

    private String duration;

    private String weatherType;

    private String weatherIcon;

    private List<String> photos;

    private String notes;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
