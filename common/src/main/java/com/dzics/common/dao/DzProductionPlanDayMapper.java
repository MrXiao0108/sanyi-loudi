package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.entity.DzProductionPlan;
import com.dzics.common.model.entity.DzProductionPlanDay;
import com.dzics.common.model.entity.PlanDayLineNo;
import com.dzics.common.model.request.plan.SelectProductionPlanRecordVo;
import com.dzics.common.model.response.charts.ActivationDetailsDo;
import com.dzics.common.model.response.plan.SelectProductionPlanRecordDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 日计划产量统计生产率 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-19
 */
@Mapper
@Repository
public interface DzProductionPlanDayMapper extends BaseMapper<DzProductionPlanDay> {

    void insertList(List<DzProductionPlan> list);

    /**
     * 查询日生产计划记录列表
     *
     * @param selectProductionPlanRecordVo
     * @return
     */
    List<SelectProductionPlanRecordDo> list(SelectProductionPlanRecordVo selectProductionPlanRecordVo);

    /**
     * 根据日期获取每日生产率 和 产线序号
     *
     * @param now
     * @return
     */
    List<PlanDayLineNo> selDateLinNo(@Param("now") LocalDate now);

    List<Map<String, Object>> planAnalysisGraphical(@Param("lineId") Long lineId,
                                                    @Param("startTime") LocalDate startTime,
                                                    @Param("endTime") LocalDate endTime,
                                                    @Param("planDay") String planDay);

    List<ActivationDetailsDo> getActivation(@Param("id") Long id,
                                            @Param("startTime") Date startTime,
                                            @Param("endTime") Date endTime,
                                            @Param("planDay") String planDay);

    void insertListSignal(List<DzProductionPlan> insertList);

    List<BigDecimal> getProductionPlanFiveDay(@Param("lineId") Long lineId, @Param("tableKey") String tableKey);

    List<Map<String, BigDecimal>> getPlanAnalysis(@Param("lineId") Long lineId, @Param("tableKey") String tableKey);

    List<PlanDayLineNo> selDateLinNoSignal(@Param("now") LocalDate now);

    List<Map<String, Object>> getLineProductionData(@Param("tableKey")String tableKey, @Param("collect")List<Long> collect, @Param("startTime")Date startTime,@Param("endTime") Date endTime);
}
