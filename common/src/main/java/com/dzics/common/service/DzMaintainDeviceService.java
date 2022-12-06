package com.dzics.common.service;

import com.dzics.common.model.entity.DzMaintainDevice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.request.device.maintain.MaintainRecordParms;
import com.dzics.common.model.response.device.maintain.MaintainDevice;
import com.dzics.common.model.response.device.maintain.MaintainRecord;
import com.dzics.common.model.response.device.maintain.MaintainRecordDetails;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 保养设备配置 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzMaintainDeviceService extends IService<DzMaintainDevice> {

    List<MaintainDevice> getMaintainList(Long lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type);

    List<MaintainDevice> getMaintainListWait(Long lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now);


    List<MaintainDevice> getMaintainListOver(Long lineId, String equipmentNo, String states, LocalDate startTime, LocalDate endTime, String field, String type, LocalDate now);

    List<MaintainRecord> getMaintainRecord(BaseTimeLimit pageLimit, MaintainRecordParms parmsReq);

    List<MaintainRecordDetails> getMaintainRecordDetails(String maintainHistoryId);
}
