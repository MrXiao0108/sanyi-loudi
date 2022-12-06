package com.dzics.data.acquisition.config.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.dzics.common.dao.DzProductionPlanDayMapper;
import com.dzics.common.model.statistics.DeviceMakeTotalDto;
import com.dzics.common.model.statistics.MakeQuantity;
import com.dzics.common.model.entity.DzEquipmentProTotalSignal;
import com.dzics.common.model.entity.PlanDayLineNo;
import com.dzics.common.service.DzEquipmentProTotalSignalService;
import com.dzics.common.service.DzProductionPlanDayService;
import com.dzics.common.service.DzProductionPlanDaySignalService;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.service.quantity.DayQuantityImpl;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每天执行一次计算昨天的生产率
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Service
@Slf4j
public class UpdateOnlyDayRateTask implements SimpleJob {
    @Autowired
    private DzEquipmentProTotalSignalService totalSignalService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    private DzProductionPlanDayService dayService;
    @Autowired
    private DzProductionPlanDaySignalService daySignalService;
    @Autowired
    private DayQuantityImpl dayQuantity;

    /**
     * 启动时更新生产率情况
     */
    @PostConstruct
    public void updateCompletionRateUpday() {
        LocalDate now = LocalDate.now().plusDays(-1);
        updateRate(now);
        initExtractedTotal(now);
    }

    public void updateRate(LocalDate now) {
        log.info("开始 UpdateOnlyDayRateTask 更新日期：{} 的生产率数据。。。。。。。。。。", now);
        String key = RedisKey.SysPlanTasDzProductionPlanDayMapperSelDateLinNok + now.toString();
        List<PlanDayLineNo> planDayLineNos = redisUtil.lGet(key, 0, -1);
        if (CollectionUtils.isEmpty(planDayLineNos)) {
            planDayLineNos = dzProductionPlanDayMapper.selDateLinNo(now);
            if (CollectionUtils.isNotEmpty(planDayLineNos)) {
                planDayLineNos = planDayLineNos.stream().filter(s -> s.getStatisticsEquimentId() != null).collect(Collectors.toList());
                redisUtil.lSet(key, planDayLineNos, 120);
            }
        }
        dayService.updateCompletionRate(now, planDayLineNos);
        String keySignal = RedisKey.SysPlanTasDzProductionPlanDayMapperSelDateLinNokSignal + now.toString();
        List<PlanDayLineNo> planDayLineNosSignal = redisUtil.lGet(keySignal, 0, -1);
        if (CollectionUtils.isEmpty(planDayLineNosSignal)) {
            planDayLineNosSignal = dzProductionPlanDayMapper.selDateLinNoSignal(now);
            if (CollectionUtils.isNotEmpty(planDayLineNosSignal)) {
                planDayLineNosSignal = planDayLineNosSignal.stream().filter(s -> s.getStatisticsEquimentId() != null).collect(Collectors.toList());
                redisUtil.lSet(keySignal, planDayLineNosSignal, 120);
            }
        }
        daySignalService.updateCompletionRate(now, planDayLineNosSignal);
        log.info("完成 UpdateOnlyDayRateTask  更新日期：{} 的生产率数据。。。。。。。。。。", now);

    }

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            int integer = Integer.parseInt(shardingContext.getShardingParameter());
            LocalDate now = LocalDate.now().plusDays(-(integer));
            updateRate(now);
        } catch (Throwable throwable) {
            log.error("计算昨日生产率异常", throwable);
        }
        try {
            updateExtractedTotal(LocalDate.now().plusDays(-1));
        } catch (Throwable throwable) {
            log.error("统计设备总产量异常", throwable);
        }

    }

    public void updateExtractedTotal(LocalDate now) {
//        统计脉冲信号  总产量
        try {
//        获取当前总产量
            List<DzEquipmentProTotalSignal> list = totalSignalService.list(Wrappers.emptyWrapper());
            if (CollectionUtils.isNotEmpty(list)) {
//        获取日总产量
                for (DzEquipmentProTotalSignal totalSignal : list) {
//        总产量+ 日产量
                    MakeQuantity signalDay = dayQuantity.getProductionQuantity(new DeviceMakeTotalDto(now, totalSignal.getDeviceId(), "dz_equipment_pro_num_signal"));
                    if (signalDay != null) {
                        Long roughNum = totalSignal.getRoughNum() + signalDay.getRoughNum();
                        Long qualifiedNum = totalSignal.getQualifiedNum() + signalDay.getQualifiedNum();
                        Long nowNum = totalSignal.getNowNum() + signalDay.getNowNum();
                        Long badnessNum = totalSignal.getBadnessNum() + signalDay.getBadnessNum();
                        totalSignal.setRoughNum(roughNum);
                        totalSignal.setQualifiedNum(qualifiedNum);
                        totalSignal.setNowNum(nowNum);
                        totalSignal.setBadnessNum(badnessNum);
                    }
                }
//        更新总产量
                totalSignalService.updateBatchById(list);
            } else {
                log.error("总产量表数据不存在无法更新 list:{}", list);
            }
        } catch (Throwable throwable) {
            log.error("统计脉冲总产量异常:{}", throwable.getMessage(), throwable);
        }
    }

    public void initExtractedTotal(LocalDate now) {
//        统计脉冲信号  总产量
        try {
//        获取当前总产量
            List<DzEquipmentProTotalSignal> list = totalSignalService.list(Wrappers.emptyWrapper());
            if (CollectionUtils.isEmpty(list)) {
                List<MakeQuantity> signalAdds = totalSignalService.sumNumber("dz_equipment_pro_num_signal", now.toString());
                if (CollectionUtils.isNotEmpty(signalAdds)) {
                    List<DzEquipmentProTotalSignal> totalSignals = new ArrayList<>();
                    for (MakeQuantity signal : signalAdds) {
                        DzEquipmentProTotalSignal totalSignal = new DzEquipmentProTotalSignal();
                        totalSignal.setDeviceId(signal.getEquimentId());
                        totalSignal.setRoughNum(signal.getRoughNum());
                        totalSignal.setQualifiedNum(signal.getQualifiedNum());
                        totalSignal.setNowNum(signal.getNowNum());
                        totalSignal.setBadnessNum(signal.getBadnessNum());
                        totalSignals.add(totalSignal);
                    }
                    totalSignalService.saveBatch(totalSignals);
                }
            }
        } catch (Throwable throwable) {
            log.error("统计脉冲总产量异常:{}", throwable.getMessage(), throwable);
        }
    }
}
