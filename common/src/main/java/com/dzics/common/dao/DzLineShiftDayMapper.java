package com.dzics.common.dao;

import com.dzics.common.model.entity.DzLineShiftDay;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.DayReportForm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * <p>
 * 设备产线 每日 排班表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-19
 */
@Mapper
@Repository
public interface DzLineShiftDayMapper extends BaseMapper<DzLineShiftDay> {

    List<DzLineShiftDay> getBc(@Param("list") List<Long> list);

    List<Long> getNotPb(@Param("now") LocalDate now);
    
    List<DayReportForm> getDayReportFormTaskSignal(@Param("now") LocalDate now);
}
