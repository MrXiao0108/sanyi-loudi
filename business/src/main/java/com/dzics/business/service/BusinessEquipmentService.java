package com.dzics.business.service;

import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.request.*;
import com.dzics.common.model.response.*;
import com.dzics.common.model.response.equipment.EquipmentAlarmDo;
import com.dzics.common.util.PageLimit;

import java.util.List;

/**
 * <p>
 * 设备表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface BusinessEquipmentService  {

    /**
     * 查询设备告警历史
     * @param sub
     * @param pageLimit
     * @param equipmentAlarmVo
     * @return
     */
    Result<List<EquipmentAlarmDo>> listAlarm(String sub, PageLimit pageLimit, EquipmentAlarmVo equipmentAlarmVo);

    //设备添加通用方法
    Result add(String sub, AddEquipmentVo addEquipmentVo);

    Result list(String sub, SelectEquipmentVo selectEquipmentVo);

    Result<EquipmentDo> getById(String sub, Long id);

    Result put(String sub, PutEquipmentVo putEquipmentVo);

    /**
     * 根据设备类型和搜索条件查询设备数据
     * @param sub
     * @param robotEquipmentCode 设备类型
     * @param selectEquipmentDataVo 查询条件
     * @return
     */
    Result<List<EquipmentDataDo>> listEquipmentData(String sub, Integer robotEquipmentCode, SelectEquipmentDataVo selectEquipmentDataVo);

    /**
     * 删除设备
     * @param sub
     * @param id
     * @return
     */
    Result del(String sub, Long id);

    Result<List<EquipmentDo>> list(String sub, Integer type, PageLimit pageLimit, SelectEquipmentVo data);

    /**
     * 根据产线id查询设备列表
     * @param sub
     * @param id
     * @return
     */
    Result<DzEquipment> getEquipmentByLineId(String sub, Long id);

    /**
     * 根据产线获取所有设备名称
     * @param lineId
     * @return
     */
    List<DzEquipment> listLingId(Long lineId);


    List<EquimentOrderLineId> getOrderLineEqId(List<String> equimentId);

    /**
     * 控制设备是否显示
     * @param sub
     * @param putIsShowVo
     * @return
     */
    Result putIsShow(String sub, PutIsShowVo putIsShowVo);

    /**
     * 根据产线获取所有设备
     * @param sub
     * @param lineId
     * @return
     */
    Result getDevcieLineId(String sub, String lineId);

    /**
     * 查询设备状态列表
     * @return
     */
    Result getEquipmentState(PageLimit pageLimit,String lineId);

    /**
     * 修改设备的数据展示状态
     * @param stateVo
     * @return
     */
    Result putEquipmentDataState(PutEquipmentDataStateVo stateVo);
}
