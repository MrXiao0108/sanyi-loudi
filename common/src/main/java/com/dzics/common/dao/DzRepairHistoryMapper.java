package com.dzics.common.dao;

import com.dzics.common.model.entity.DzRepairHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.FaultRecordDetailsInner;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备故障维修单 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzRepairHistoryMapper extends BaseMapper<DzRepairHistory> {

    List<FaultRecord> getFaultRecordList(@Param("checkNumber") Long checkNumber, @Param("lineId") Long lineId, @Param("faultType") Integer faultType, @Param("equipmentNo") String equipmentNo, @Param("field") String field, @Param("type") String type,@Param("startTime")Date startTime,@Param("endTime") Date endTime);


    List<FaultRecordDetailsInner> getFaultRecordDetails(@Param("repairId") String repairId);
}
