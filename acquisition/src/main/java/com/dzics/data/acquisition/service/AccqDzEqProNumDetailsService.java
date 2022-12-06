package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.UpValueDevice;
import com.dzics.common.model.entity.DzEquipmentProNumDetails;

/**
 * 数量详情接口
 *
 * @author ZhangChengJun
 * Date 2021/1/19.
 * @since
 */
public interface AccqDzEqProNumDetailsService {
    /**
     * @param deviceNumber         设备序号
     * @param productionLineNumber 产线编号
     * @param deviceType           设备类型
     * @return 最近存储的数据
     */
    DzEquipmentProNumDetails getUpSave(String deviceNumber, String productionLineNumber, String deviceType);

    /**
     * @param details 存储生产数据详情
     */
    void saveDataDetails(DzEquipmentProNumDetails details);

    /**
     * @param lineNum 产线序号
     * @param deviceNum 设备序号
     * @param deviceType 设备类型
     * @param orderNumber
     * @return
     */
    UpValueDevice getupsavenumlinenuty(String lineNum, String deviceNum, String deviceType, String orderNumber);

}
