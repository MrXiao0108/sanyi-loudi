package com.dzics.common.service;

import com.dzics.common.model.entity.DayDailyReport;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.plan.DayDailyReportExcel;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日产报表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
public interface DayDailyReportService extends IService<DayDailyReport> {

    List<DayDailyReportExcel> getDayDailyReport(String field, String type, LocalDate endTime, LocalDate startTime);

}
