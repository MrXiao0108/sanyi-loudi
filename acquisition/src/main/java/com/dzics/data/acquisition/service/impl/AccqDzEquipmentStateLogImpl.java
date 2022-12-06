package com.dzics.data.acquisition.service.impl;

import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.dzics.common.service.DzEquipmentStateLogService;
import com.dzics.data.acquisition.service.AccqDzEquipmentStateLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
@Service
public class AccqDzEquipmentStateLogImpl implements AccqDzEquipmentStateLog {
    @Autowired
    private DzEquipmentStateLogService stateLogService;

    @Override
    public void saveRunSatetLog(DzEquipmentStateLog stateLog) {
        stateLogService.save(stateLog);
    }
}
