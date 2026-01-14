package com.hiking.infrastructure.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云OSS配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
public class OssProperties {

    /**
     * OSS访问域名
     */
    private String endpoint;

    /**
     * 存储空间名称
     */
    private String bucketName;

    /**
     * AccessKey ID
     */
    private String accessKeyId;

    /**
     * AccessKey Secret
     */
    private String accessKeySecret;

    /**
     * 徒步记录资源目录
     */
    private String recordsResourceDir;

    /**
     * 获取OSS访问URL前缀
     */
    public String getUrlPrefix() {
        return "https://" + bucketName + "." + endpoint + "/";
    }
}
