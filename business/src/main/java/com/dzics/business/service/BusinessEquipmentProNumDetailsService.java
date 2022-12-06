package com.dzics.business.service;

import com.dzics.common.model.request.charts.RobotDataChartsListVo;
import com.dzics.common.model.response.Result;

public interface BusinessEquipmentProNumDetailsService {
    /**
     * 查询机器人生产数据走势图
     * @param robotDataChartsListVo
     * @return
     */
    Result list(RobotDataChartsListVo robotDataChartsListVo);
}
