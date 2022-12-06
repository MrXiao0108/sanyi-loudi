package com.dzics.common.service;

import com.dzics.common.model.custom.UpValueDevice;
import com.dzics.common.model.entity.DzEquipmentProNumDetails;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 设备生产数量详情表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzEquipmentProNumDetailsService extends IService<DzEquipmentProNumDetails> {


    /**
     * @param lineNum 产线序号
     * @param deviceNum 设备序号
     * @param deviceType 设备类型
     * @param orderNumber
     * @return
     */
    UpValueDevice getupsaveddnumlinnuty(String lineNum, String deviceNum, String deviceType, String orderNumber);

    void delProNumDetails(int i);
}
