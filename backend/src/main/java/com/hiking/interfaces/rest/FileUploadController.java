package com.hiking.interfaces.rest;

import com.hiking.infrastructure.oss.OssService;
import com.hiking.interfaces.rest.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "文件上传相关接口")
public class FileUploadController {

    private final OssService ossService;

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传单张图片", description = "上传单张徒步记录图片到OSS")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            validateImageFile(file);
            String url = ossService.uploadRecordImage(file);
            return ApiResponse.success(url);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return ApiResponse.error(500, "图片上传失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "批量上传图片", description = "批量上传徒步记录图片到OSS")
    public ApiResponse<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                return ApiResponse.error(400, "请选择要上传的图片");
            }
            
            if (files.length > 9) {
                return ApiResponse.error(400, "最多只能上传9张图片");
            }

            List<String> urls = new ArrayList<>();
            for (MultipartFile file : files) {
                validateImageFile(file);
                String url = ossService.uploadRecordImage(file);
                urls.add(url);
            }
            
            return ApiResponse.success(urls);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return ApiResponse.error(500, "图片上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/image")
    @Operation(summary = "删除图片", description = "删除OSS上的图片")
    public ApiResponse<Void> deleteImage(@RequestParam("url") String url) {
        try {
            ossService.deleteFile(url);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("图片删除失败", e);
            return ApiResponse.error(500, "图片删除失败: " + e.getMessage());
        }
    }

    /**
     * 验证图片文件
     */
    private void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("文件不能为空");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("只支持图片文件");
        }

        // 限制文件大小为10MB
        long maxSize = 10 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("图片大小不能超过10MB");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = originalFilename.toLowerCase();
            if (!extension.endsWith(".jpg") && !extension.endsWith(".jpeg") 
                    && !extension.endsWith(".png") && !extension.endsWith(".gif")
                    && !extension.endsWith(".webp")) {
                throw new IllegalArgumentException("只支持 jpg、jpeg、png、gif、webp 格式的图片");
            }
        }
    }
}
