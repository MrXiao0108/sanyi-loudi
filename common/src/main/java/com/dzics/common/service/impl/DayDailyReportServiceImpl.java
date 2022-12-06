package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.DayDailyReportMapper;
import com.dzics.common.model.entity.DayDailyReport;
import com.dzics.common.model.response.plan.DayDailyReportExcel;
import com.dzics.common.service.DayDailyReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日产报表 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
@Service
public class DayDailyReportServiceImpl extends ServiceImpl<DayDailyReportMapper, DayDailyReport> implements DayDailyReportService {

    @Override
    public List<DayDailyReportExcel> getDayDailyReport(String field, String type, LocalDate endTime, LocalDate startTime) {
        if (endTime != null) {
            endTime = endTime.plusDays(1L);
        }
        return this.baseMapper.getDayDailyReport(field,type,endTime, startTime);
    }
}
