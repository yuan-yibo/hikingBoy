package com.hiking.domain.hiking.repository;

import com.hiking.domain.hiking.entity.HikingRecord;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 徒步记录仓储接口
 * <p>
 * 仓储是 DDD 中的重要概念，定义在领域层，
 * 提供聚合的持久化操作抽象，具体实现在基础设施层。
 */
public interface HikingRecordRepository {

    /**
     * 保存徒步记录
     */
    HikingRecord save(HikingRecord record);

    /**
     * 根据ID查找徒步记录
     */
    Optional<HikingRecord> findById(Long id);

    /**
     * 根据用户ID查找所有记录（按创建时间降序）
     */
    List<HikingRecord> findByUserId(String userId);

    /**
     * 根据用户ID和日期范围查找记录
     */
    List<HikingRecord> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据ID删除记录
     */
    void deleteById(Long id);

    /**
     * 检查记录是否存在
     */
    boolean existsById(Long id);

    /**
     * 统计用户的记录数量
     */
    long countByUserId(String userId);

    /**
     * 计算用户的总徒步距离
     */
    Double sumDistanceByUserId(String userId);

    /**
     * 根据所有者ID查找记录
     */
    List<HikingRecord> findByOwnerId(Long ownerId);

    /**
     * 根据所有者ID列表查找记录（用于团队共享）
     */
    List<HikingRecord> findByOwnerIdIn(List<Long> ownerIds);

    /**
     * 根据团队ID查找记录
     */
    List<HikingRecord> findByTeamId(Long teamId);
}
