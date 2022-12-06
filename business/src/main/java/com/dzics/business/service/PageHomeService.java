package com.dzics.business.service;

import com.dzics.common.model.response.Result;

public interface PageHomeService {
    /**
     * 产出率和合格率
     * @param lineId
     * @return
     */
    Result getOutputAndQualified(Long lineId);

    /**
     * 首页查询产线日产和月产
     * @param lineId
     * @return
     */
    Result geDayAndMonthData(Long lineId);

    /**
     * 首页查询设备信息
     * @param lineId
     * @return
     */
    Result geEquipmentState(Long lineId);

    Result geDayAndMonthDataV2(Long lineId);

}
