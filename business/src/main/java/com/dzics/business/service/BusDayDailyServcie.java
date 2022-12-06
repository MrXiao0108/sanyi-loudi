package com.dzics.business.service;

import com.dzics.common.model.entity.DayDailyReport;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.request.base.SearchTimeBase;
import com.dzics.common.model.response.DayReportForm;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.DayDailyReportExcel;

import java.util.List;

/**
 * 日产报表
 *
 * @author ZhangChengJun
 * Date 2021/6/23.
 * @since
 */
public interface BusDayDailyServcie {

    /**
     * 保存设备日产数据
     * @param dayReportForms
     * @return
     */
    boolean saveDayDayReport(List<DayReportForm> dayReportForms);

    Result<List<DayDailyReportExcel>> dayDailyReport(String sub, BaseTimeLimit timeBase);
}
