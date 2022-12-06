package com.dzics.mqtt.dao;

import com.dzics.mqtt.model.entity.DzDataDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 数据采集设备表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-22
 */
public interface DzDataDeviceMapper extends BaseMapper<DzDataDevice> {

    List<DzDataDevice> getByType(int ty);

}
