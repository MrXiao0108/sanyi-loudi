package com.dzics.data.acquisition.service;

import com.dzics.common.model.entity.DzEquipmentProNum;

/**
 * 班次生产记录表
 *
 * @author ZhangChengJun
 * Date 2021/1/19.
 * @since
 */
public interface AcqDzEqProDataService {

    /**
     * 保存
     * @param proNum 产生数据班车记录
     */
    DzEquipmentProNum saveDzEqProNum(DzEquipmentProNum proNum);

    /**
     * @param dzEqProNum 更新班次生产记录
     */
    void updateDzEqProNum(DzEquipmentProNum dzEqProNum);
}
