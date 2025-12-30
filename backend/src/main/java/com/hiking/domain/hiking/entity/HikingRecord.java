package com.hiking.domain.hiking.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.hiking.domain.hiking.valueobject.Weather;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 徒步记录聚合根
 * <p>
 * 聚合根是 DDD 中的核心概念，它是聚合的入口点，
 * 负责维护聚合内部的一致性和不变性规则。
 */
@TableName(value = "hiking_record", autoResultMap = true)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HikingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID（微信 openid）
     */
    @TableField("user_id")
    private String userId;

    /**
     * 关联的用户ID（数据库主键）
     */
    @TableField("owner_id")
    private Long ownerId;

    /**
     * 团队ID（可选，用于团队共享）
     */
    @TableField("team_id")
    private Long teamId;

    /**
     * 山名
     */
    @TableField("mountain_name")
    private String mountainName;

    /**
     * 徒步日期
     */
    @TableField("hiking_date")
    private LocalDate hikingDate;

    /**
     * 距离（公里）
     */
    @TableField("distance")
    private Double distance;

    /**
     * 时长（小时）
     */
    @TableField("duration")
    private String duration;

    /**
     * 天气类型
     */
    @TableField("weather_type")
    private String weatherType;

    /**
     * 天气图标
     */
    @TableField("weather_icon")
    private String weatherIcon;

    /**
     * 照片列表（JSON存储）
     */
    @TableField(value = "photos", typeHandler = com.hiking.infrastructure.persistence.typehandler.JsonListTypeHandler.class)
    private List<String> photos = new ArrayList<>();

    /**
     * 笔记
     */
    @TableField("notes")
    private String notes;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建徒步记录的工厂方法
     */
    public static HikingRecord create(String userId, String mountainName, LocalDate hikingDate,
                                       Double distance, String duration, Weather weather,
                                       List<String> photos, String notes) {
        return create(userId, null, null, mountainName, hikingDate, distance, duration, weather, photos, notes);
    }

    /**
     * 创建徒步记录的工厂方法（带团队和所有者ID）
     */
    public static HikingRecord create(String userId, Long ownerId, Long teamId, String mountainName, LocalDate hikingDate,
                                       Double distance, String duration, Weather weather,
                                       List<String> photos, String notes) {
        HikingRecord record = new HikingRecord();
        record.userId = userId;
        record.ownerId = ownerId;
        record.teamId = teamId;
        record.mountainName = mountainName;
        record.hikingDate = hikingDate;
        record.distance = distance;
        record.duration = formatDuration(duration);
        if (weather != null) {
            record.weatherType = weather.getType();
            record.weatherIcon = weather.getIcon();
        }
        record.photos = photos != null ? new ArrayList<>(photos) : new ArrayList<>();
        record.notes = notes;
        record.createTime = LocalDateTime.now();
        record.updateTime = LocalDateTime.now();
        
        // 验证业务规则
        record.validate();
        
        return record;
    }

    /**
     * 更新徒步记录
     */
    public void update(String mountainName, LocalDate hikingDate, Double distance,
                       String duration, Weather weather, List<String> photos, String notes) {
        this.mountainName = mountainName;
        this.hikingDate = hikingDate;
        this.distance = distance;
        this.duration = formatDuration(duration);
        if (weather != null) {
            this.weatherType = weather.getType();
            this.weatherIcon = weather.getIcon();
        } else {
            this.weatherType = null;
            this.weatherIcon = null;
        }
        this.photos = photos != null ? new ArrayList<>(photos) : new ArrayList<>();
        this.notes = notes;
        this.updateTime = LocalDateTime.now();
        
        // 验证业务规则
        validate();
    }

    /**
     * 获取天气值对象
     */
    public Weather getWeather() {
        return Weather.of(weatherType, weatherIcon);
    }

    /**
     * 设置团队ID
     */
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 设置所有者ID
     */
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 添加照片
     */
    public void addPhoto(String photoUrl) {
        if (this.photos.size() >= 9) {
            throw new IllegalStateException("照片数量不能超过9张");
        }
        this.photos.add(photoUrl);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 移除照片
     */
    public void removePhoto(String photoUrl) {
        this.photos.remove(photoUrl);
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 获取照片列表（不可变）
     */
    public List<String> getPhotos() {
        return Collections.unmodifiableList(photos);
    }

    /**
     * 验证业务规则
     */
    private void validate() {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        if (mountainName == null || mountainName.isBlank()) {
            throw new IllegalArgumentException("山名不能为空");
        }
        if (hikingDate == null) {
            throw new IllegalArgumentException("徒步日期不能为空");
        }
        if (hikingDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("徒步日期不能是未来日期");
        }
    }

    /**
     * 格式化时长：如果是纯数字，自动添加"h"
     */
    private static String formatDuration(String duration) {
        if (duration == null || duration.isBlank()) {
            return null;
        }
        if (duration.matches("^\\d+(\\.\\d+)?$")) {
            return duration + "h";
        }
        return duration;
    }
}
