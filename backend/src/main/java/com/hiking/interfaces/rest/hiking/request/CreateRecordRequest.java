package com.hiking.interfaces.rest.hiking.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 创建徒步记录请求
 */
@Data
public class CreateRecordRequest {

    @NotBlank(message = "山名不能为空")
    private String mountainName;

    @NotNull(message = "徒步日期不能为空")
    private LocalDate hikingDate;

    private Double distance;

    private String duration;

    private String weatherType;

    private String weatherIcon;

    private List<String> photos;

    private String notes;
}
