package com.dzics.common.service;

import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 设备运行状态记录表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
public interface DzEquipmentStateLogService extends IService<DzEquipmentStateLog> {

    void delEquimentLog(Integer delEquipmentLog);

}
