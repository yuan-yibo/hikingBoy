package com.hiking.application.hiking.service;

import com.hiking.application.hiking.assembler.HikingRecordAssembler;
import com.hiking.application.hiking.command.CreateHikingRecordCommand;
import com.hiking.application.hiking.command.UpdateHikingRecordCommand;
import com.hiking.application.hiking.dto.HikingRecordDTO;
import com.hiking.application.team.service.TeamApplicationService;
import com.hiking.domain.hiking.entity.HikingRecord;
import com.hiking.domain.hiking.repository.HikingRecordRepository;
import com.hiking.domain.hiking.service.HikingRecordDomainService;
import com.hiking.domain.hiking.valueobject.Weather;
import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 徒步记录应用服务
 * <p>
 * 应用服务是 DDD 中的核心组件，负责：
 * 1. 协调领域对象完成业务用例
 * 2. 事务管理
 * 3. 调用领域服务
 * 4. DTO 与领域对象的转换
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HikingRecordApplicationService {

    private final HikingRecordRepository hikingRecordRepository;
    private final HikingRecordDomainService hikingRecordDomainService;
    private final HikingRecordAssembler hikingRecordAssembler;
    private final TeamApplicationService teamApplicationService;
    private final UserRepository userRepository;

    /**
     * 根据 openId 获取或创建用户，返回用户ID
     */
    private Long getOrCreateUserId(String openId) {
        return userRepository.findByOpenId(openId)
                .map(User::getId)
                .orElseGet(() -> {
                    User newUser = User.create(openId, "徒步爱好者", null);
                    userRepository.save(newUser);
                    return newUser.getId();
                });
    }

    /**
     * 创建徒步记录
     */
    public HikingRecordDTO createRecord(CreateHikingRecordCommand command) {
        return createRecord(command, command.getUserId());
    }

    /**
     * 创建徒步记录（带所有者 openId）
     */
    public HikingRecordDTO createRecord(CreateHikingRecordCommand command, String visitorId) {
        Long ownerId = getOrCreateUserId(visitorId);
        
        // 创建天气值对象
        Weather weather = Weather.of(command.getWeatherType(), command.getWeatherIcon());
        
        // 通过聚合根的工厂方法创建实体
        HikingRecord record = HikingRecord.create(
                command.getUserId(),
                ownerId,
                null, // teamId
                command.getMountainName(),
                command.getHikingDate(),
                command.getDistance(),
                command.getDuration(),
                weather,
                command.getPhotos(),
                command.getNotes()
        );
        
        // 持久化
        HikingRecord savedRecord = hikingRecordRepository.save(record);
        
        // 返回 DTO
        return hikingRecordAssembler.toDTO(savedRecord);
    }

    /**
     * 更新徒步记录
     */
    public HikingRecordDTO updateRecord(UpdateHikingRecordCommand command) {
        // 查找记录
        HikingRecord record = hikingRecordRepository.findById(command.getId())
                .orElseThrow(() -> new IllegalArgumentException("记录不存在: " + command.getId()));
        
        // 权限校验
        if (!hikingRecordDomainService.canUserAccessRecord(command.getUserId(), record)) {
            throw new IllegalStateException("无权操作此记录");
        }
        
        // 创建天气值对象
        Weather weather = Weather.of(command.getWeatherType(), command.getWeatherIcon());
        
        // 更新记录
        record.update(
                command.getMountainName(),
                command.getHikingDate(),
                command.getDistance(),
                command.getDuration(),
                weather,
                command.getPhotos(),
                command.getNotes()
        );
        
        // 持久化
        HikingRecord savedRecord = hikingRecordRepository.save(record);
        
        return hikingRecordAssembler.toDTO(savedRecord);
    }

    /**
     * 删除徒步记录
     */
    public void deleteRecord(Long id, String userId) {
        HikingRecord record = hikingRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("记录不存在: " + id));
        
        // 权限校验
        if (!hikingRecordDomainService.canUserAccessRecord(userId, record)) {
            throw new IllegalStateException("无权操作此记录");
        }
        
        hikingRecordRepository.deleteById(id);
    }

    /**
     * 根据ID查询记录
     */
    @Transactional(readOnly = true)
    public HikingRecordDTO getRecordById(Long id) {
        HikingRecord record = hikingRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("记录不存在: " + id));
        return hikingRecordAssembler.toDTO(record);
    }

    /**
     * 查询用户的所有记录
     */
    @Transactional(readOnly = true)
    public List<HikingRecordDTO> getRecordsByUserId(String userId) {
        List<HikingRecord> records = hikingRecordRepository.findByUserId(userId);
        return hikingRecordAssembler.toDTOList(records);
    }

    /**
     * 获取用户统计信息
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStatistics(String userId) {
        return hikingRecordDomainService.getUserStatistics(userId);
    }

    /**
     * 根据所有者ID查询记录
     */
    @Transactional(readOnly = true)
    public List<HikingRecordDTO> getRecordsByOwnerId(Long ownerId) {
        List<HikingRecord> records = hikingRecordRepository.findByOwnerId(ownerId);
        return hikingRecordAssembler.toDTOList(records);
    }

    /**
     * 获取团队成员的所有记录
     */
    @Transactional(readOnly = true)
    public List<HikingRecordDTO> getTeamRecords(String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        // 获取用户所在团队的所有成员ID
        List<Long> memberUserIds = teamApplicationService.getTeamMemberUserIds(userId);
        if (memberUserIds.isEmpty()) {
            return List.of();
        }
        
        // 查询这些成员的所有记录
        List<HikingRecord> records = hikingRecordRepository.findByOwnerIdIn(memberUserIds);
        return hikingRecordAssembler.toDTOList(records);
    }

    /**
     * 根据团队ID查询记录
     */
    @Transactional(readOnly = true)
    public List<HikingRecordDTO> getRecordsByTeamId(Long teamId) {
        List<HikingRecord> records = hikingRecordRepository.findByTeamId(teamId);
        return hikingRecordAssembler.toDTOList(records);
    }
}
