package com.dzics.common.service;

import com.dzics.common.model.custom.UpValueDeviceSignal;
import com.dzics.common.model.entity.DzEquipmentProNumDetailsSignal;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 设备生产数量详情表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
public interface DzEquipmentProNumDetailsSignalService extends IService<DzEquipmentProNumDetailsSignal> {

    UpValueDeviceSignal getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber, Long dayId);
}
