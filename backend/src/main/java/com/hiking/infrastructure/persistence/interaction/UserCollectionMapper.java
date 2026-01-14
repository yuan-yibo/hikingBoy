package com.hiking.infrastructure.persistence.interaction;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserCollectionMapper extends BaseMapper<UserCollectionDO> {
}
