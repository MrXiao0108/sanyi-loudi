package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.UpValueDeviceSignal;
import com.dzics.common.model.entity.DzEquipmentProNumDetailsSignal;

/**
 * @author NeverEnd
 * @since 2021-02-23
 */
public interface AccqDzEqProDetailsSignalService {

    /**
     * @param lineNum
     * @param deviceNum
     * @param deviceType
     * @param orderNumber
     * @param dayId
     * @return
     */
    UpValueDeviceSignal getUpSaveDdNumLinNuTy(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId);

    /**
     * @param details
     */
    void saveDataDetails(DzEquipmentProNumDetailsSignal details);
}
