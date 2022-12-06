package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzMaintainDeviceMapper;
import com.dzics.common.model.entity.DzMaintainDevice;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.request.device.maintain.MaintainRecordParms;
import com.dzics.common.model.response.device.maintain.MaintainDevice;
import com.dzics.common.model.response.device.maintain.MaintainRecord;
import com.dzics.common.model.response.device.maintain.MaintainRecordDetails;
import com.dzics.common.service.DzMaintainDeviceService;
import com.github.pagehelper.PageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 保养设备配置 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzMaintainDeviceServiceImpl extends ServiceImpl<DzMaintainDeviceMapper, DzMaintainDevice> implements DzMaintainDeviceService {

    @Override
    public List<MaintainDevice> getMaintainList(Long lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type) {
        return baseMapper.getMaintainList(lineId, equipmentNo, states, startTime, endTime, field, type);
    }

    @Override
    public List<MaintainDevice> getMaintainListWait(Long lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now) {
        return baseMapper.getMaintainListWait(lineId, equipmentNo, states, startTime, endTime, field, type, now);
    }

    @Override
    public List<MaintainDevice> getMaintainListOver(Long lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now) {
        return baseMapper.getMaintainListOver(lineId, equipmentNo, states, startTime, endTime, field, type, now);
    }

    @Override
    public List<MaintainRecord> getMaintainRecord(BaseTimeLimit pageLimit, MaintainRecordParms parmsReq) {
        List<MaintainRecord> maintainRecord = baseMapper.getMaintainRecord(pageLimit.getStartTime(), pageLimit.getEndTime(), pageLimit.getField(), pageLimit.getType(), parmsReq.getCreateBy(), parmsReq.getMaintainId());
        return maintainRecord;
    }

    @Override
    public List<MaintainRecordDetails> getMaintainRecordDetails(String maintainHistoryId) {
        return baseMapper.getMaintainRecordDetails(maintainHistoryId);
    }
}
