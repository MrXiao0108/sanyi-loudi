package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzRepairHistoryMapper;
import com.dzics.common.model.entity.DzRepairHistory;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.FaultRecordDetailsInner;
import com.dzics.common.service.DzRepairHistoryService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备故障维修单 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzRepairHistoryServiceImpl extends ServiceImpl<DzRepairHistoryMapper, DzRepairHistory> implements DzRepairHistoryService {

    @Override
    public List<FaultRecord> getFaultRecordList(Long checkNumber, Long lineId, Integer faultType, String equipmentNo, String field, String type, Date startTime, Date endTime) {
        return baseMapper.getFaultRecordList(checkNumber, lineId, faultType, equipmentNo, field, type,startTime,endTime);
    }

    @Override
    public List<FaultRecordDetailsInner> getFaultRecordDetails(String repairId) {
        return baseMapper.getFaultRecordDetails(repairId);
    }
}
