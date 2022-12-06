package com.dzics.data.acquisition.config.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzProductionPlanDayMapper;
import com.dzics.common.model.entity.PlanDayLineNo;
import com.dzics.common.service.DzProductionPlanDayService;
import com.dzics.common.service.DzProductionPlanDaySignalService;
import com.dzics.common.util.RedisKey;
import com.dzics.data.acquisition.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 更新生产率数据
 *
 * @author ZhangChengJun
 * Date 2021/11/19.
 * @since
 */
@Service
@Slf4j
public class UpdateCompletionRateTask implements SimpleJob {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private DzProductionPlanDayMapper dzProductionPlanDayMapper;
    @Autowired
    private DzProductionPlanDayService dayService;
    @Autowired
    private DzProductionPlanDaySignalService daySignalService;

    /**
     * 每3分钟 运算一次当日的生产完成率
     */
    public void updateCompletionRate() {
        log.debug("开始 执行任务更新生产率数据。。。。。。。。。。");
        LocalDate now = LocalDate.now();
        updateRate(now);
        log.debug("完成 执行任务更新生产率数据。。。。。。。。。。");
    }


    public void updateRate(LocalDate now) {
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
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        updateCompletionRate();
    }
}
