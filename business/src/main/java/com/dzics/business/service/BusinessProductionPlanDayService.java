package com.dzics.business.service;

import com.dzics.common.model.request.charts.ActivationVo;
import com.dzics.common.model.request.mom.LineProductionDataVO;
import com.dzics.common.model.request.plan.SelectProductionPlanRecordVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

import java.util.Date;
import java.util.List;

public interface BusinessProductionPlanDayService {
    /**
     * 查询日生产计划记录列表
     * @param sub
     * @param pageLimit
     * @param selectProductionPlanRecordVo
     * @return
     */
    Result list(String sub, PageLimit pageLimit, SelectProductionPlanRecordVo selectProductionPlanRecordVo);

    /**
     * 查询日生产计划记录详情列表
     * @param planId
     * @param detectorTime
     * @return
     */
    Result detailsList(Long planId, String detectorTime);

    /**
     * 产线稼动率分析
     * @param sub
     * @param activationVo
     * @return
     */
    Result activation(String sub, ActivationVo activationVo);

    /**
     * 获取产线日生产数据
     * @return
     */
    Result getLineProductionData(LineProductionDataVO lineProductionDataVO);
}
