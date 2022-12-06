package com.dzics.data.acquisition.config.task;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.entity.DzLineShiftDay;
import com.dzics.common.service.DzLineShiftDayService;
import com.dzics.common.util.RedisKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;

/**
 * 系统任务
 *
 * @author ZhangChengJun
 * Date 2021/1/19.
 * @since
 */
@Service
@Slf4j
public class SysBusTask implements SimpleJob {
    @Autowired
    private DzLineShiftDayService dzLineShiftDayService;
    @Autowired
    public RedissonClient redissonClient;

    /**
     * 定时设备排班
     */
    @PostConstruct
    public void arrange() {
        log.info("开支执行排班任务。。。。。。。。。。");
        RLock lock = redissonClient.getLock(RedisKey.SYS_BUS_TASK_ARRANGE);
        try {
            lock.lock();
//            查询未排班的设备
//            当天班次排班
            LocalDate now = LocalDate.now();
            String substring = now.toString().substring(0, 7);
            int year = now.getYear();
            List<Long> eqId = dzLineShiftDayService.getNotPb(now);
            if (CollectionUtils.isNotEmpty(eqId)) {
                List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayService.getBc(eqId);
                dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                    dzLineShiftDay.setWorkData(now);
                    dzLineShiftDay.setWorkYear(year);
                    dzLineShiftDay.setWorkMouth(substring);
                });
                dzLineShiftDayService.saveBatch(dzLineShiftDays);
            } else {
                log.warn("日期：{} 已排班", now);
            }
//            当天班次加+ 1 排班
            LocalDate localDate = LocalDate.now().plusDays(1L);
            String substringAdd = localDate.toString().substring(0, 7);
            int yearAdd = localDate.getYear();
            List<Long> eqNext = dzLineShiftDayService.getNotPb(localDate);
            if (CollectionUtils.isNotEmpty(eqNext)) {
                List<DzLineShiftDay> dzLineShiftDays = dzLineShiftDayService.getBc(eqNext);
                dzLineShiftDays.stream().forEach(dzLineShiftDay -> {
                    dzLineShiftDay.setWorkData(localDate);
                    dzLineShiftDay.setWorkMouth(substringAdd);
                    dzLineShiftDay.setWorkYear(yearAdd);
                });
                dzLineShiftDayService.saveBatch(dzLineShiftDays);
            }else {
                log.warn("日期：{} 已排班", now);
            }
        } catch (Throwable e) {
            log.error("排班时发生错误：{}", e.getMessage(), e);
        } finally {
            lock.unlock();
        }
        log.info("执行排班任务结束。。。。。。。。。。");
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("排班任务执行开始。。。。。。。。。。。。");
        arrange();
        log.info("排班任务执行结束。。。。。。。。。。。。");
    }
}
