package com.hiking.infrastructure.persistence.repository;

import com.hiking.domain.hiking.entity.HikingRecord;
import com.hiking.domain.hiking.repository.HikingRecordRepository;
import com.hiking.infrastructure.persistence.mapper.HikingRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 徒步记录仓储实现
 * <p>
 * 基础设施层实现领域层定义的仓储接口
 */
@Repository
@RequiredArgsConstructor
public class HikingRecordRepositoryImpl implements HikingRecordRepository {

    private final HikingRecordMapper hikingRecordMapper;

    @Override
    public HikingRecord save(HikingRecord record) {
        if (record.getId() == null) {
            hikingRecordMapper.insert(record);
        } else {
            hikingRecordMapper.updateById(record);
        }
        return record;
    }

    @Override
    public Optional<HikingRecord> findById(Long id) {
        return Optional.ofNullable(hikingRecordMapper.selectById(id));
    }

    @Override
    public List<HikingRecord> findByUserId(String userId) {
        return hikingRecordMapper.selectByUserIdOrderByCreateTimeDesc(userId);
    }

    @Override
    public List<HikingRecord> findByUserIdAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return hikingRecordMapper.selectByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public void deleteById(Long id) {
        hikingRecordMapper.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return hikingRecordMapper.selectById(id) != null;
    }

    @Override
    public long countByUserId(String userId) {
        return hikingRecordMapper.countByUserId(userId);
    }

    @Override
    public Double sumDistanceByUserId(String userId) {
        return hikingRecordMapper.sumDistanceByUserId(userId);
    }

    @Override
    public List<HikingRecord> findByOwnerId(Long ownerId) {
        return hikingRecordMapper.selectByOwnerId(ownerId);
    }

    @Override
    public List<HikingRecord> findByOwnerIdIn(List<Long> ownerIds) {
        if (ownerIds == null || ownerIds.isEmpty()) {
            return List.of();
        }
        return hikingRecordMapper.selectByOwnerIdIn(ownerIds);
    }

    @Override
    public List<HikingRecord> findByTeamId(Long teamId) {
        return hikingRecordMapper.selectByTeamId(teamId);
    }
}
