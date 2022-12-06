package com.dzics.common.service;

import com.dzics.common.model.custom.LineNumberTotal;
import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.custom.SocketProQuantity;
import com.dzics.common.model.entity.DzEquipmentProNum;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备生产数量表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
public interface DzEquipmentProNumService extends IService<DzEquipmentProNum> {

    List<MachiningNumTotal> getEqIdData(LocalDate now, List<String> collect, String tableKey);
    /**
     * 排班任务
     */
    void arrange();


    /**
     * @param id 班次id
     * @param productType
     * @param batchNumber
     * @param modelNumber
     * @param hour
     * @return 班次生产记录聚合数据
     */
    DzEquipmentProNum getByDayId(Long id, String productType, String batchNumber, String modelNumber, int hour);

    DzEquipmentProNum getDzEquipmentProNum(Long id);

    /**
     * 产线日产
     * @param now
     * @param eqId
     * @param tableKey
     * @param systemConfig
     * @return
     */
    LineNumberTotal getLineSumQuantity(LocalDate now, Long eqId, String tableKey, String systemConfig);

    /**
     * 获取订单的下设备的 当日 投入 产出 不良品 数量
     * @return
     */
    List<SocketProQuantity> getInputOutputDefectiveProducts(String tableKey, LocalDate now, String orderNo, String lineNo);
}
