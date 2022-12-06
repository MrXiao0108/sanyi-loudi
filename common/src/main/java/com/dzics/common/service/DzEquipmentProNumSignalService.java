package com.dzics.common.service;

import com.dzics.common.model.entity.DzEquipmentProNumSignal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;

/**
 * <p>
 * 班次生产记录表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
public interface DzEquipmentProNumSignalService extends IService<DzEquipmentProNumSignal> {

    DzEquipmentProNumSignal getByDayId(Long id, String productType, String batchNumber, String modelNumber, int hour);

    void updateDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal);

    DzEquipmentProNumSignal saveDzEqProNum(DzEquipmentProNumSignal dzEquipmentProNumSignal);


    Long getEquimentIdDayProNum(Long id, LocalDate nowDay, String tableKey);
}
