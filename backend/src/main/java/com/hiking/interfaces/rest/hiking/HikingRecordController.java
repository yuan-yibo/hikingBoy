package com.hiking.interfaces.rest.hiking;

import com.hiking.application.hiking.command.CreateHikingRecordCommand;
import com.hiking.application.hiking.command.UpdateHikingRecordCommand;
import com.hiking.application.hiking.dto.HikingRecordDTO;
import com.hiking.application.hiking.service.HikingRecordApplicationService;
import com.hiking.interfaces.rest.common.ApiResponse;
import com.hiking.interfaces.rest.hiking.request.CreateRecordRequest;
import com.hiking.interfaces.rest.hiking.request.UpdateRecordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 徒步记录 REST 控制器
 * <p>
 * 接口层负责处理 HTTP 请求，将请求转换为应用层命令/查询
 */
@RestController
@RequestMapping("/v1/hiking-records")
@RequiredArgsConstructor
@Tag(name = "徒步记录", description = "徒步记录管理接口")
public class HikingRecordController {

    private final HikingRecordApplicationService hikingRecordApplicationService;

    @PostMapping
    @Operation(summary = "创建徒步记录")
    public ApiResponse<HikingRecordDTO> createRecord(
            @RequestHeader("X-User-Id") String userId,
            @Valid @RequestBody CreateRecordRequest request) {
        
        CreateHikingRecordCommand command = new CreateHikingRecordCommand();
        command.setUserId(userId);
        command.setMountainName(request.getMountainName());
        command.setHikingDate(request.getHikingDate());
        command.setDistance(request.getDistance());
        command.setDuration(request.getDuration());
        command.setWeatherType(request.getWeatherType());
        command.setWeatherIcon(request.getWeatherIcon());
        command.setPhotos(request.getPhotos());
        command.setNotes(request.getNotes());
        
        HikingRecordDTO result = hikingRecordApplicationService.createRecord(command, userId);
        return ApiResponse.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新徒步记录")
    public ApiResponse<HikingRecordDTO> updateRecord(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecordRequest request) {
        
        UpdateHikingRecordCommand command = new UpdateHikingRecordCommand();
        command.setId(id);
        command.setUserId(userId);
        command.setMountainName(request.getMountainName());
        command.setHikingDate(request.getHikingDate());
        command.setDistance(request.getDistance());
        command.setDuration(request.getDuration());
        command.setWeatherType(request.getWeatherType());
        command.setWeatherIcon(request.getWeatherIcon());
        command.setPhotos(request.getPhotos());
        command.setNotes(request.getNotes());
        
        HikingRecordDTO result = hikingRecordApplicationService.updateRecord(command);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除徒步记录")
    public ApiResponse<Void> deleteRecord(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long id) {
        
        hikingRecordApplicationService.deleteRecord(id, userId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取徒步记录详情")
    public ApiResponse<HikingRecordDTO> getRecord(
            @PathVariable @Parameter(description = "记录ID") Long id) {
        
        HikingRecordDTO result = hikingRecordApplicationService.getRecordById(id);
        return ApiResponse.success(result);
    }

    @GetMapping
    @Operation(summary = "获取用户的所有徒步记录")
    public ApiResponse<List<HikingRecordDTO>> getRecords(
            @RequestHeader("X-User-Id") String userId) {
        
        List<HikingRecordDTO> result = hikingRecordApplicationService.getRecordsByUserId(userId);
        return ApiResponse.success(result);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取用户徒步统计")
    public ApiResponse<Map<String, Object>> getStatistics(
            @RequestHeader("X-User-Id") String userId) {
        
        Map<String, Object> result = hikingRecordApplicationService.getUserStatistics(userId);
        return ApiResponse.success(result);
    }

    @GetMapping("/team")
    @Operation(summary = "获取团队成员的所有徒步记录")
    public ApiResponse<List<HikingRecordDTO>> getTeamRecords(
            @RequestHeader("X-User-Id") String visitorId) {
        
        List<HikingRecordDTO> result = hikingRecordApplicationService.getTeamRecords(visitorId);
        return ApiResponse.success(result);
    }

    @GetMapping("/by-owner/{ownerId}")
    @Operation(summary = "根据所有者ID获取记录")
    public ApiResponse<List<HikingRecordDTO>> getRecordsByOwnerId(
            @PathVariable Long ownerId) {
        
        List<HikingRecordDTO> result = hikingRecordApplicationService.getRecordsByOwnerId(ownerId);
        return ApiResponse.success(result);
    }
}
