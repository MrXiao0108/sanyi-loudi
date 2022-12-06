package com.dzics.common.service;

import com.dzics.common.model.entity.DzRepairHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.FaultRecordDetailsInner;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备故障维修单 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzRepairHistoryService extends IService<DzRepairHistory> {

    List<FaultRecord> getFaultRecordList(Long checkNumber, Long lineId, Integer faultType, String equipmentNo, String field, String type, Date startTime, Date endTime);

    List<FaultRecordDetailsInner> getFaultRecordDetails(String repairId);
}
