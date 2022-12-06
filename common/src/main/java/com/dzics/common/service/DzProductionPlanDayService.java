package com.dzics.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.DzProductionPlanDay;
import com.dzics.common.model.entity.PlanDayLineNo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日计划产量统计生产率 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
public interface DzProductionPlanDayService extends IService<DzProductionPlanDay> {

    /**
     * 生成每日计划基础数据
     */
    @Transactional(rollbackFor = Throwable.class)
    void datRunMeth(LocalDate now);


    /**
     * 更新生产率
     */
    void updateCompletionRate(LocalDate localDate,List<PlanDayLineNo> planDayLineNos);


    List<Map<String, Object>> planAnalysisGraphical(Long lineId, LocalDate startTime, LocalDate endTime,String planDay);
}
