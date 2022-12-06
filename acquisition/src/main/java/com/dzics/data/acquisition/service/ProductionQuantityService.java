package com.dzics.data.acquisition.service;

import com.dzics.common.model.response.EquipmentAvailableDo;
import com.dzics.common.model.response.PlanAnalysisDo;
import com.dzics.common.model.response.Result;

import java.math.BigDecimal;
import java.util.List;

/**
 * 中兴液压数字看板
 */
public interface ProductionQuantityService {

    /**
     * 根据产线获取绑定设备的五日内产量
     * @param lineId
     * @return
     */
    Result getOutputByLineId(Long lineId);

    /**
     * 根据产线查询所有设备当日用时分析
     */
    Result getEquipmentAvailable(Long lineId);

    /**
     * 根据产线id查询近五日稼动率
     * @param lineId
     * @return
     */
    Result getProductionPlanFiveDay(Long lineId);

    /**
     * 根据产线id查询近五日产线计划分析
     * @return
     */
    Result  getPlanAnalysis(Long lineId);

    /**
     * 刀具信息数据
     * @param orderNo
     * @param lineNo
     * @return
     */
    Result  getToolInfoData(String orderNo,String lineNo);


}
