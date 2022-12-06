package com.dzics.common.service;

import com.dzics.common.model.statistics.MakeQuantity;
import com.dzics.common.model.entity.DzEquipmentProTotalSignal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 设备总生产数量 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2022-06-22
 */
public interface DzEquipmentProTotalSignalService extends IService<DzEquipmentProTotalSignal> {

    List<MakeQuantity> sumNumber(String table, String toString);
}
