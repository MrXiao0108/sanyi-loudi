package com.dzics.data.acquisition.service.impl;

import com.dzics.common.model.custom.UpValueDeviceSignal;
import com.dzics.common.model.entity.DzEquipmentProNumDetailsSignal;
import com.dzics.common.service.DzEquipmentProNumDetailsSignalService;
import com.dzics.data.acquisition.service.AccqDzEqProDetailsSignalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author ZhangChengJun
 * Date 2021/2/23.
 * @since
 */
@Slf4j
@Service
public class AccqDzEqProDetailsSignalServiceImpl implements AccqDzEqProDetailsSignalService {
    @Autowired
    private DzEquipmentProNumDetailsSignalService detailsSignalService;
    @Override
    public UpValueDeviceSignal getUpSaveDdNumLinNuTy(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId) {
        return detailsSignalService.getupsaveddnumlinnuty(lineNum, deviceNum, deviceType,orderNumber,dayId);
    }

    @Override
    public void saveDataDetails(DzEquipmentProNumDetailsSignal details) {
        details.setCreateTime(new Date());
        detailsSignalService.save(details);
    }
}
