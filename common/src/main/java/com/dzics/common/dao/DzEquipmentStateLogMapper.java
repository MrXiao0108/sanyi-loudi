package com.dzics.common.dao;

import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 设备运行状态记录表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Mapper
@Repository
public interface DzEquipmentStateLogMapper extends BaseMapper<DzEquipmentStateLog> {

}
