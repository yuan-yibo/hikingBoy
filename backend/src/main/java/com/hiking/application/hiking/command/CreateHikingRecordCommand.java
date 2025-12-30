package com.hiking.application.hiking.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 创建徒步记录命令
 * <p>
 * 命令对象用于封装创建操作的输入参数，
 * 是 CQRS 模式中的核心概念。
 */
@Data
public class CreateHikingRecordCommand {

    /**
     * 用户ID
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
