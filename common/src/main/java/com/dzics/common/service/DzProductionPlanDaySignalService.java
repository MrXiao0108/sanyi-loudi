package com.dzics.common.service;

import com.dzics.common.model.entity.DzProductionPlanDaySignal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.entity.PlanDayLineNo;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日计划产量统计生产率 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
public interface DzProductionPlanDaySignalService extends IService<DzProductionPlanDaySignal> {

    /**
     * 更新生产率
     * @param now
     */
    void updateCompletionRate(LocalDate now, List<PlanDayLineNo> planDayLineNos);
    /**
     * 生成每日计划基础数据
     * @param now
     */
    @Transactional(rollbackFor = Throwable.class)
    void datRunMeth(LocalDate now);

}
