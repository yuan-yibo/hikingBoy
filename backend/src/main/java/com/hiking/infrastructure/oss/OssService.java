package com.hiking.infrastructure.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 阿里云OSS服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OssService {

    private final OSS ossClient;
    private final OssProperties ossProperties;

    /**
     * 上传徒步记录图片
     *
     * @param file 图片文件
     * @return 图片访问URL
     */
    public String uploadRecordImage(MultipartFile file) throws IOException {
        return uploadFile(file, ossProperties.getRecordsResourceDir());
    }

    /**
     * 上传文件到指定目录
     *
     * @param file      文件
     * @param directory 目录名
     * @return 文件访问URL
     */
    public String uploadFile(MultipartFile file, String directory) throws IOException {
        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String newFileName = generateFileName(extension);
        
        // 构建完整的对象路径: directory/yyyy/MM/dd/uuid.extension
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectKey = directory + "/" + datePath + "/" + newFileName;

        // 设置文件元数据
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        // 上传文件
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossProperties.getBucketName(),
                    objectKey,
                    inputStream,
                    metadata
            );
            ossClient.putObject(putObjectRequest);
            
            String url = ossProperties.getUrlPrefix() + objectKey;
            log.info("文件上传成功: {}", url);
            return url;
        }
    }

    /**
     * 上传文件（通过输入流）
     *
     * @param inputStream 输入流
     * @param directory   目录名
     * @param contentType 内容类型
     * @param extension   文件扩展名
     * @return 文件访问URL
     */
    public String uploadFile(InputStream inputStream, String directory, String contentType, String extension) {
        String newFileName = generateFileName(extension);
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String objectKey = directory + "/" + datePath + "/" + newFileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossProperties.getBucketName(),
                objectKey,
                inputStream,
                metadata
        );
        ossClient.putObject(putObjectRequest);

        String url = ossProperties.getUrlPrefix() + objectKey;
        log.info("文件上传成功: {}", url);
        return url;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }
        
        String urlPrefix = ossProperties.getUrlPrefix();
        if (fileUrl.startsWith(urlPrefix)) {
            String objectKey = fileUrl.substring(urlPrefix.length());
            ossClient.deleteObject(ossProperties.getBucketName(), objectKey);
            log.info("文件删除成功: {}", objectKey);
        }
    }

    /**
     * 检查文件是否存在
     *
     * @param fileUrl 文件URL
     * @return 是否存在
     */
    public boolean doesFileExist(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        
        String urlPrefix = ossProperties.getUrlPrefix();
        if (fileUrl.startsWith(urlPrefix)) {
            String objectKey = fileUrl.substring(urlPrefix.length());
            return ossClient.doesObjectExist(ossProperties.getBucketName(), objectKey);
        }
        return false;
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ".jpg";
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0) {
            return filename.substring(dotIndex);
        }
        return ".jpg";
    }
}
