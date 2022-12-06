package com.dzics.data.acquisition.config.task;

import com.dzics.common.dao.DzLineShiftDayMapper;
import com.dzics.common.model.response.DayReportForm;
import com.dzics.data.acquisition.service.AccDayDailyServcie;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * 日报表
 *
 * @author ZhangChengJun
 * Date 2021/6/22.
 * @since
 */
@Service
@Slf4j
public class ReportFormTask implements SimpleJob {

    @Autowired
    private DzLineShiftDayMapper dzLineShiftDayMapper;

    @Autowired
    private AccDayDailyServcie accDayDailyServcie;

    /**
     * 日报表任务每次凌晨生成昨天的报表
     */
    public void dayReportFormTask() {
        LocalDate now = LocalDate.now().plusDays(-1);
        boolean exist = accDayDailyServcie.getWorkDate(now);
        if (exist) {
            List<DayReportForm> dayReportForms = dzLineShiftDayMapper.getDayReportFormTaskSignal(now);
            if (CollectionUtils.isEmpty(dayReportForms)) {
                log.warn("任务执行插入日产：{},统计数据：dayReportForms：{} 无数据", now, dayReportForms);
                return;
            }
            boolean tr = accDayDailyServcie.saveDayDayReport(dayReportForms);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.debug("生成日产报表任务执行日期：{}", LocalDate.now());
        dayReportFormTask();
    }
}
