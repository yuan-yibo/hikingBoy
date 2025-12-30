package com.hiking.domain.hiking.service;

import com.hiking.domain.hiking.entity.HikingRecord;
import com.hiking.domain.hiking.repository.HikingRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 徒步记录领域服务
 * <p>
 * 领域服务用于处理跨多个聚合或不适合放在单个实体中的业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class HikingRecordDomainService {

    private final HikingRecordRepository hikingRecordRepository;

    /**
     * 获取用户徒步统计信息
     */
    public Map<String, Object> getUserStatistics(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总记录数
        long totalRecords = hikingRecordRepository.countByUserId(userId);
        stats.put("totalRecords", totalRecords);
        
        // 总徒步距离
        Double totalDistance = hikingRecordRepository.sumDistanceByUserId(userId);
        stats.put("totalDistance", totalDistance != null ? totalDistance : 0.0);
        
        // 本月记录数
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        List<HikingRecord> monthRecords = hikingRecordRepository.findByUserIdAndDateRange(userId, monthStart, now);
        stats.put("monthlyRecords", monthRecords.size());
        
        // 本月徒步距离
        double monthlyDistance = monthRecords.stream()
                .filter(r -> r.getDistance() != null)
                .mapToDouble(HikingRecord::getDistance)
                .sum();
        stats.put("monthlyDistance", monthlyDistance);
        
        return stats;
    }

    /**
     * 验证用户是否有权限操作记录
     */
    public boolean canUserAccessRecord(String userId, HikingRecord record) {
        return record != null && record.getUserId().equals(userId);
    }
}
