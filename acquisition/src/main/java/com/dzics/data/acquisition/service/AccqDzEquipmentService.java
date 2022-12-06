package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.SocketUtilization;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.response.JCEquiment;
import com.dzics.data.acquisition.model.index.IndexBaseType;

import java.util.List;

/**
 * 设备接口类
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public interface AccqDzEquipmentService {
    /**
     * @param dzEquipment 根据设置序号和产线序号更新设备状态
     */
    int updateByLineNoAndEqNo(DzEquipment dzEquipment);

    /**
     * @param deviceNumber 设备序号
     * @param productionLineNumber 产线序号
     * @param deviceType 设备类型
     * @param orderNumber
     * @return
     */
    DzEquipment getTypeLingEqNo(String deviceNumber, String productionLineNumber, String deviceType, String orderNumber);

    /**
     * @return 所有设备
     */
    List<DzEquipment> list();

    /**
     * @return 所有设备
     */
    List<JCEquiment> listMap();

    DzEquipment getEqNoEqType(String eqNo, Integer eqType,String orderNo,String lineNo);

    IndexBaseType getByEquiMentId(String deviceId);
    IndexBaseType getByEquiMentIdDown(String deviceId);

    List<DzEquipment> getRunStaTimeIsNotNull();

    /**
     * 根据订单产线获取所有设备
     * @param orderNo
     * @param lineNo
     * @return
     */
    List<DzEquipment> getDeviceOrderNoLineNo(String orderNo, String lineNo);


    Integer getDeviceSignalvalue(String orderNumber, String lineNum, String deviceType, String deviceNum);

    int updateByLineNoAndEqNoDownTime(DzEquipment downEq);

}
