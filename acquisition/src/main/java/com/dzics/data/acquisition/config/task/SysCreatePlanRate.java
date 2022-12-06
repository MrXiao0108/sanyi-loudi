package com.dzics.data.acquisition.config.task;

import com.dzics.common.service.DzProductionPlanDayService;
import com.dzics.common.service.DzProductionPlanDaySignalService;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;

/**
 * 生产日产率基础数据
 *
 * @author ZhangChengJun
 * Date 2021/2/19.
 * @since
 */
@Component
@Slf4j
public class SysCreatePlanRate implements SimpleJob {
    @Autowired
    private DzProductionPlanDayService dayService;
    @Autowired
    private DzProductionPlanDaySignalService daySignalService;


    /**
     * 生成每日统计生产率基础数据 每天23:55 执行生成明天计划
     */
    public void datRunMeth() {
        log.info("开始执行任务 生成每日统计生产率基础数据。。。。。。。。。。");
        LocalDate nowNext = LocalDate.now().plusDays(1);
        dayService.datRunMeth(nowNext);
        daySignalService.datRunMeth(nowNext);
        log.info("执行任务完成 生成每日统计生产率基础数据。。。。。。。。。。");
    }


    /**
     * 生成每日统计生产率基础数据  启动的时候执行
     */
    @PostConstruct
    public void datRunMethResetRun() {
        LocalDate now = LocalDate.now();
        dayService.datRunMeth(now);
        daySignalService.datRunMeth(now);
    }


    @Override
    public void execute(ShardingContext shardingContext) {
        datRunMeth();
    }
}
