package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentAlarmRecordMapper;
import com.dzics.common.model.entity.DzEquipmentAlarmRecord;
import com.dzics.common.service.DzEquipmentAlarmRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备报警记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-09
 */
@Service
@Slf4j
public class DzEquipmentAlarmRecordServiceImpl extends ServiceImpl<DzEquipmentAlarmRecordMapper, DzEquipmentAlarmRecord> implements DzEquipmentAlarmRecordService {

}
