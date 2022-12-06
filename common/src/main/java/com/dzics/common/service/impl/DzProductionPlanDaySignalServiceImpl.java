package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DzEquipmentProNumMapper;
import com.dzics.common.dao.DzProductionPlanDayMapper;
import com.dzics.common.dao.DzProductionPlanDaySignalMapper;
import com.dzics.common.enums.PlanType;
import com.dzics.common.enums.ProductionPlanEnum;
import com.dzics.common.model.entity.DzProductionPlan;
import com.dzics.common.model.entity.DzProductionPlanDaySignal;
import com.dzics.common.model.entity.PlanDayLineNo;
import com.dzics.common.service.DzProductionPlanDaySignalService;
import com.dzics.common.service.DzProductionPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 日计划产量统计生产率 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-23
 */
@Service
public class DzProductionPlanDaySignalServiceImpl extends ServiceImpl<DzProductionPlanDaySignalMapper, DzProductionPlanDaySignal> implements DzProductionPlanDaySignalService {
    @Autowired
    private DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    private DzProductionPlanDaySignalMapper daySignalMapper;
    @Autowired
    private DzEquipmentProNumMapper proNumMapper;
    @Autowired
    private DzProductionPlanService planService;

   /* @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateCompletionRate(LocalDate localDate) {
        List<DzProductionPlanDaySignal> days = new ArrayList<>();
        List<PlanDayLineNo> planDayLineNos = dzProductionPlanDayMapper.selDateLinNo(localDate);
        for (PlanDayLineNo planDayLineNo : planDayLineNos) {
//            完成数量
            Map<String, BigDecimal> numAll = proNumMapper.workNowLocalDateSignal(localDate, planDayLineNo.getLineNo(), planDayLineNo.getStatisticsEquimentId());
            if (numAll != null) {
//            生产数量
                BigDecimal nowNumBig = numAll.get("nowNum");
//            合格数量
                BigDecimal qualifiedNum = numAll.get("qualifiedNum");
//            毛坯数量
                BigDecimal roughNum = numAll.get("roughNum");
//            产出率计算
                BigDecimal outputRate;
                BigDecimal passRate;
                if (roughNum.compareTo(new BigDecimal(0)) == 0) {
                    outputRate = new BigDecimal(0);
                } else {
//                产出率
                    outputRate = nowNumBig.divide(roughNum, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                }
                if (qualifiedNum.compareTo(new BigDecimal(0)) == 0) {
                    passRate = new BigDecimal(0);
                } else {
                    // 合格率
                    passRate = roughNum.divide(qualifiedNum, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                }
//            计划数量
                Integer plannedQuantity = planDayLineNo.getPlannedQuantity();
                BigDecimal planBig = new BigDecimal(plannedQuantity);
//            完成率
                BigDecimal bigDecimal = new BigDecimal(0);
                if (planBig.compareTo(new BigDecimal(0)) != 0) {
                    bigDecimal = nowNumBig.divide(planBig, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                }

                DzProductionPlanDaySignal day = new DzProductionPlanDaySignal();
                day.setPlanDayId(planDayLineNo.getPlanDayId());
                day.setCompletedQuantity(nowNumBig.longValue());
                day.setPercentageComplete(bigDecimal);
                day.setOutputRate(outputRate);
                day.setPassRate(passRate);
                days.add(day);
            }

        }
        if (CollectionUtils.isNotEmpty(days)) {
            updateBatchById(days);
        }
    }*/

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void updateCompletionRate(LocalDate localDate, List<PlanDayLineNo> planDayLineNos) {
        List<DzProductionPlanDaySignal> days = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(planDayLineNos)) {
            List<Long> longList = planDayLineNos.stream().map(s -> s.getStatisticsEquimentId()).collect(Collectors.toList());
            List<Map<String, Object>> numIds = proNumMapper.workNowLocalDateSignalIds(localDate, longList);
            for (PlanDayLineNo planDayLineNo : planDayLineNos) {
                Map<String, Object> numAll = null;
                for (Map<String, Object> stringBigDecimalMap : numIds) {
                    Long bigDecimal = (Long) stringBigDecimalMap.get("equimentId");
                    if (bigDecimal.longValue() == planDayLineNo.getStatisticsEquimentId().longValue()) {
                        numAll = stringBigDecimalMap;
                        break;
                    } else {
                        continue;
                    }
                }
                if (numAll != null) {
                    //        产出数量
                    BigDecimal nowNumBig = (BigDecimal) numAll.get("nowNum");
                    //            合格数量
                    BigDecimal qualifiedNum = (BigDecimal) numAll.get("qualifiedNum");
                    //            毛坯数量
                    BigDecimal roughNum = (BigDecimal) numAll.get("roughNum");
                    //            产出率计算
                    BigDecimal outputRate;
                    BigDecimal passRate;
                    if (roughNum.compareTo(new BigDecimal(0)) == 0) {
                        outputRate = new BigDecimal(0);
                    } else {
                        //                产出率
                        outputRate = nowNumBig.divide(roughNum, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    }
                    if (nowNumBig.compareTo(new BigDecimal(0)) == 0) {
                        passRate = new BigDecimal(0);
                    } else {
                        // 合格率
                        passRate = qualifiedNum.divide(nowNumBig, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    }
                    //            计划数量
                    Integer plannedQuantity = planDayLineNo.getPlannedQuantity();
                    BigDecimal planBig = new BigDecimal(plannedQuantity);
                    //            完成率
                    BigDecimal bigDecimal = new BigDecimal(0);
                    if (planBig.compareTo(new BigDecimal(0)) != 0) {
                        bigDecimal = nowNumBig.divide(planBig, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100));
                    }

                    DzProductionPlanDaySignal day = new DzProductionPlanDaySignal();
                    day.setPlanDayId(planDayLineNo.getPlanDayId());
                    day.setCompletedQuantity(nowNumBig.longValue());
                    day.setPercentageComplete(bigDecimal);
                    day.setOutputRate(outputRate);
                    day.setPassRate(passRate);
                    days.add(day);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(days)) {
            updateBatchById(days);
        }
    }

    @Override
    public void datRunMeth(LocalDate now) {
        QueryWrapper<DzProductionPlan> wp = new QueryWrapper<>();
        wp.select("id", "planned_quantity", "org_code");
        wp.eq("plan_type", PlanType.DAY.getCode());
        wp.eq("status", ProductionPlanEnum.Enable.getCode());
        List<DzProductionPlan> list = planService.list(wp);
//        当日计划是否已产生
        List<DzProductionPlanDaySignal> days = new ArrayList<>();
        for (DzProductionPlan dzProductionPlan : list) {
            QueryWrapper<DzProductionPlanDaySignal> wpPalnDay = new QueryWrapper<>();
            wpPalnDay.eq("plan_id", dzProductionPlan.getId());
            wpPalnDay.eq("detector_time", now);
            List<DzProductionPlanDaySignal> planDay = list(wpPalnDay);
            if (CollectionUtils.isEmpty(planDay)) {
                DzProductionPlanDaySignal day = new DzProductionPlanDaySignal();
                day.setPlanId(dzProductionPlan.getId());
                day.setPlannedQuantity(dzProductionPlan.getPlannedQuantity());
                day.setDetectorTime(now);
                day.setCompletedQuantity(0L);
                day.setPercentageComplete(new BigDecimal("0"));
                day.setOutputRate(new BigDecimal("0"));
                day.setPassRate(new BigDecimal("0"));
                day.setOrgCode(dzProductionPlan.getOrgCode());
                day.setDelFlag(false);
                day.setCreateTime(new Date());
                days.add(day);
            }
        }
        if (CollectionUtils.isNotEmpty(days)){
            saveBatch(days);
        }
    }
}
