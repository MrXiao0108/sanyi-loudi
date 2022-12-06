package com.dzics.common.dao;

import com.dzics.common.model.entity.DzDataDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.sany.SanyDeviceData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 数据采集设备表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-21
 */
@Mapper
@Repository
public interface DzDataDeviceMapper extends BaseMapper<DzDataDevice> {

    List<SanyDeviceData> getSanyDevice(@Param("deviceId") Long deviceId,
                                       @Param("deviceName") String deviceName,
                                       @Param("deviceType") Integer deviceType,
                                       @Param("deviceTypeCode") String deviceTypeCode,
                                       @Param("orderNo") String orderNo,
                                       @Param("lineNo") String lineNo,
                                       @Param("field") String field,
                                       @Param("type") String type);


}
