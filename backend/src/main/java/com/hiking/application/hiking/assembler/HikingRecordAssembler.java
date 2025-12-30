package com.hiking.application.hiking.assembler;

import com.hiking.application.hiking.dto.HikingRecordDTO;
import com.hiking.domain.hiking.entity.HikingRecord;
import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 徒步记录装配器
 * <p>
 * 负责领域对象和 DTO 之间的转换
 */
@Component
@RequiredArgsConstructor
public class HikingRecordAssembler {

    private final UserRepository userRepository;

    /**
     * 领域实体转 DTO
     */
    public HikingRecordDTO toDTO(HikingRecord record) {
        if (record == null) {
            return null;
        }

        HikingRecordDTO dto = new HikingRecordDTO();
        dto.setId(record.getId());
        dto.setUserId(record.getUserId());
        dto.setOwnerId(record.getOwnerId());
        dto.setTeamId(record.getTeamId());
        
        // 获取所有者名称
        if (record.getOwnerId() != null) {
            userRepository.findById(record.getOwnerId())
                    .ifPresent(user -> dto.setOwnerName(user.getNickname()));
        }
        
        dto.setMountainName(record.getMountainName());
        dto.setHikingDate(record.getHikingDate());
        dto.setDistance(record.getDistance());
        dto.setDuration(record.getDuration());
        dto.setWeatherType(record.getWeatherType());
        dto.setWeatherIcon(record.getWeatherIcon());
        dto.setPhotos(new ArrayList<>(record.getPhotos()));
        dto.setNotes(record.getNotes());
        dto.setCreateTime(record.getCreateTime());
        dto.setUpdateTime(record.getUpdateTime());
        
        return dto;
    }

    /**
     * 领域实体列表转 DTO 列表
     */
    public List<HikingRecordDTO> toDTOList(List<HikingRecord> records) {
        if (records == null) {
            return new ArrayList<>();
        }
        return records.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
