package com.dzics.data.acquisition.service;

import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.response.DayReportForm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.DayDailyReportExcel;

import java.time.LocalDate;
import java.util.List;

/**
 * 日产报表
 *
 * @author ZhangChengJun
 * Date 2021/6/23.
 * @since
 */
public interface AccDayDailyServcie {

    /**
     * 保存设备日产数据
     * @param dayReportForms
     * @return
     */
    boolean saveDayDayReport(List<DayReportForm> dayReportForms);

    Result<List<DayDailyReportExcel>> dayDailyReport(String sub, BaseTimeLimit timeBase);

    /**
     * 日产报表是否存在
     * @param now
     * @return
     */
    boolean getWorkDate(LocalDate now);
}
