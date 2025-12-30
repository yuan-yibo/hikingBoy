package com.hiking.interfaces.rest;

import com.hiking.application.share.dto.GenerateShareRequest;
import com.hiking.application.share.dto.PlatformDTO;
import com.hiking.application.share.dto.ShareContentDTO;
import com.hiking.application.share.service.ShareApplicationService;
import com.hiking.interfaces.rest.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分享接口
 */
@RestController
@RequestMapping("/v1/share")
@RequiredArgsConstructor
public class ShareController {
    
    private final ShareApplicationService shareApplicationService;
    
    /**
     * 获取支持的分享平台列表
     */
    @GetMapping("/platforms")
    public ApiResponse<List<PlatformDTO>> getPlatforms() {
        List<PlatformDTO> platforms = shareApplicationService.getSupportedPlatforms();
        return ApiResponse.success(platforms);
    }
    
    /**
     * 生成分享文案
     */
    @PostMapping("/generate")
    public ApiResponse<ShareContentDTO> generateShareContent(
            @RequestHeader("X-User-Id") String visitorId,
            @RequestBody GenerateShareRequest request) {
        ShareContentDTO content = shareApplicationService.generateShareContent(
                request.getRecordId(), 
                request.getPlatform()
        );
        return ApiResponse.success(content);
    }
    
    /**
     * 根据记录ID和平台生成文案（GET方式，方便测试）
     */
    @GetMapping("/generate/{recordId}/{platform}")
    public ApiResponse<ShareContentDTO> generateShareContentGet(
            @PathVariable Long recordId,
            @PathVariable String platform,
            @RequestHeader("X-User-Id") String visitorId) {
        ShareContentDTO content = shareApplicationService.generateShareContent(recordId, platform);
        return ApiResponse.success(content);
    }
}
