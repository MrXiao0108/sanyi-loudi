package com.dzics.common.dao;

import com.dzics.common.model.entity.DayDailyReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.plan.DayDailyReportExcel;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 日产报表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-23
 */
public interface DayDailyReportMapper extends BaseMapper<DayDailyReport> {

    List<DayDailyReportExcel> getDayDailyReport(@Param("field") String field, @Param("type") String type, @Param("endTime") LocalDate endTime, @Param("startTime") LocalDate startTime);
}
