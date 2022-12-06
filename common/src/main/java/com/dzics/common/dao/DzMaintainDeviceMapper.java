package com.dzics.common.dao;

import com.dzics.common.model.entity.DzMaintainDevice;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.device.maintain.MaintainDevice;
import com.dzics.common.model.response.device.maintain.MaintainRecord;
import com.dzics.common.model.response.device.maintain.MaintainRecordDetails;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 保养设备配置 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzMaintainDeviceMapper extends BaseMapper<DzMaintainDevice> {

    List<MaintainDevice> getMaintainList(@Param("lineId") Long lineId, @Param("equipmentNo") String equipmentNo,
                                         @Param("states") String states, @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime,
                                         @Param("field") String field, @Param("type") String type);

    List<MaintainDevice> getMaintainListWait(@Param("lineId") Long lineId, @Param("equipmentNo") String equipmentNo, @Param("states") String states,
                                             @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("field") String field,
                                             @Param("type") String type, @Param("now") LocalDate now);


    List<MaintainDevice> getMaintainListOver(@Param("lineId") Long lineId, @Param("equipmentNo") String equipmentNo, @Param("states") String states,
                                             @Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("field") String field,
                                             @Param("type") String type, @Param("now") LocalDate now);

    List<MaintainRecord> getMaintainRecord(@Param("startTime") LocalDate startTime, @Param("endTime") LocalDate endTime, @Param("field") String field,
                                           @Param("type") String type, @Param("createBy") String createBy, @Param("maintainId") String maintainId);

    List<MaintainRecordDetails> getMaintainRecordDetails(@Param("maintainHistoryId") String maintainHistoryId);
}
