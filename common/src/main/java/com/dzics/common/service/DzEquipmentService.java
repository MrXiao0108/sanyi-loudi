package com.dzics.common.service;

import com.dzics.common.model.custom.MachiningMessageStatus;
import com.dzics.common.model.custom.SocketUtilization;
import com.dzics.common.model.entity.DzEquipment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.JCEquiment;
import com.dzics.common.model.response.equipmentstate.DzDataCollectionDo;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzEquipmentService extends IService<DzEquipment> {

    /**
     * @param dzEquipment 根据设置序号和产线序号更新设备状态
     */
    int updateByLineNoAndEqNo(DzEquipment dzEquipment);

    List<JCEquiment> listjcjqr();

    /**
     * 获取所有有开始运行时间的设备
     * @return
     */
    List<DzEquipment> getRunStaTimeIsNotNull();

    /**
     *
     * @param deviceId
     * @param localDate
     * @return
     */
    DzEquipment listjcjqrdeviceid(Long deviceId, LocalDate localDate);


    List<DzDataCollectionDo> getMachiningMessageStatus(String lineNo, String orderNum, LocalDate now);

    List<DzEquipment> getDeviceOrderNoLineNo(String orderNo, String lineNo);

    SocketUtilization getSocketUtilization(DzEquipment dzEquipment);

    /**
     * 多个设备稼动率
     * @param orderNo
     * @param lineNo
     * @return
     */
    List<SocketUtilization> getSocketUtilizationList(String orderNo, String lineNo);

    int updateByLineNoAndEqNoDownTime(DzEquipment downEq);

}
