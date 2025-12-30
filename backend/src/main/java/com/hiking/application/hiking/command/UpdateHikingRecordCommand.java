package com.hiking.application.hiking.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 更新徒步记录命令
 */
@Data
public class UpdateHikingRecordCommand {

    /**
     * 记录ID
     */
    @NotNull(message = "记录ID不能为空")
    private Long id;

    /**
     * 用户ID（用于权限校验）
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

    /**
     * 山名
     */
    @NotBlank(message = "山名不能为空")
    private String mountainName;

    /**
     * 徒步日期
     */
    @NotNull(message = "徒步日期不能为空")
    private LocalDate hikingDate;

    /**
     * 距离（公里）
     */
    private Double distance;

    /**
     * 时长
     */
    private String duration;

    /**
     * 天气类型
     */
    private String weatherType;

    /**
     * 天气图标
     */
    private String weatherIcon;

    /**
     * 照片列表
     */
    private List<String> photos;

    /**
     * 笔记
     */
    private String notes;
}
