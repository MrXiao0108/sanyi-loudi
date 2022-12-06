package com.dzics.business.service;

import com.dzics.business.model.vo.detectordata.ProcessingBeat;
import com.dzics.business.model.vo.plan.PlanAnalysisGraphical;
import com.dzics.business.model.vo.plan.PlanAnalysisGraphicalTime;
import com.dzics.common.model.request.charts.IntelligentDetectionVo;
import com.dzics.common.model.request.plan.SelectProductionPlanVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.ProductionPlanDo;
import com.dzics.common.util.PageLimit;

import java.text.ParseException;

public interface BusinessProductionPlanService {
    Result list(String sub, PageLimit pageLimit, SelectProductionPlanVo selectProductionPlanVo);

    Result<ProductionPlanDo> put(String sub, ProductionPlanDo productionPlanDo);

    /**
     * 产线计划分析图形数据
     *
     * @param sub
     * @param graphical
     * @return
     */
    Result<?> planAnalysisGraphical(String sub, PlanAnalysisGraphical graphical);

    /**
     * 设备用时分析
     *
     * @param sub
     * @param graphical
     * @return
     */
    Result equipmentTimeAnalysis(String sub, PlanAnalysisGraphical graphical);

    /**
     * 设备加工节拍
     *
     * @param sub
     * @param processingBeat
     * @return
     */
    Result getProcessingBeat(String sub, ProcessingBeat processingBeat);

    Result equipmentTimeAnalysisV2(String sub, PlanAnalysisGraphicalTime graphical);

    /**
     * 后台管理-智能检测系统查询-查询最新的一条检测数据
     * @param sub
     * @param intelligentDetectionVo
     * @return
     */
    Result intelligentDetection(String sub, IntelligentDetectionVo intelligentDetectionVo) throws ParseException;
}
