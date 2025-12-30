package com.hiking.infrastructure.persistence.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hiking.domain.hiking.entity.HikingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 徒步记录 MyBatis-Plus Mapper
 */
@Mapper
public interface HikingRecordMapper extends BaseMapper<HikingRecord> {

    /**
     * 根据用户ID查找记录（按创建时间降序）
     */
    @Select("SELECT * FROM hiking_record WHERE user_id = #{userId} ORDER BY create_time DESC")
    @ResultMap("mybatis-plus_HikingRecord")
    List<HikingRecord> selectByUserIdOrderByCreateTimeDesc(@Param("userId") String userId);

    /**
     * 根据用户ID和日期范围查找记录
     */
    @Select("SELECT * FROM hiking_record WHERE user_id = #{userId} " +
            "AND hiking_date >= #{startDate} AND hiking_date <= #{endDate} " +
            "ORDER BY create_time DESC")
    @ResultMap("mybatis-plus_HikingRecord")
    List<HikingRecord> selectByUserIdAndDateRange(
            @Param("userId") String userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 统计用户记录数
     */
    @Select("SELECT COUNT(*) FROM hiking_record WHERE user_id = #{userId}")
    long countByUserId(@Param("userId") String userId);

    /**
     * 计算用户总徒步距离
     */
    @Select("SELECT COALESCE(SUM(distance), 0) FROM hiking_record WHERE user_id = #{userId}")
    Double sumDistanceByUserId(@Param("userId") String userId);

    /**
     * 根据所有者ID列表查询记录（用于团队共享）
     */
    @Select("<script>SELECT * FROM hiking_record WHERE owner_id IN " +
            "<foreach collection='ownerIds' item='id' open='(' separator=',' close=')'>#{id}</foreach> " +
            "ORDER BY create_time DESC</script>")
    @ResultMap("mybatis-plus_HikingRecord")
    List<HikingRecord> selectByOwnerIdIn(@Param("ownerIds") List<Long> ownerIds);

    /**
     * 根据所有者ID查询记录
     */
    @Select("SELECT * FROM hiking_record WHERE owner_id = #{ownerId} ORDER BY create_time DESC")
    @ResultMap("mybatis-plus_HikingRecord")
    List<HikingRecord> selectByOwnerId(@Param("ownerId") Long ownerId);

    /**
     * 根据团队ID查询记录
     */
    @Select("SELECT * FROM hiking_record WHERE team_id = #{teamId} ORDER BY create_time DESC")
    @ResultMap("mybatis-plus_HikingRecord")
    List<HikingRecord> selectByTeamId(@Param("teamId") Long teamId);
}
