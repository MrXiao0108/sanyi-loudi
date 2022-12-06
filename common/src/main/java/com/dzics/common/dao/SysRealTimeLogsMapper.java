package com.dzics.common.dao;

import com.dzics.common.model.entity.SysRealTimeLogs;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.ReatimLogRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 设备运行告警日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Mapper
@Repository
public interface SysRealTimeLogsMapper extends BaseMapper<SysRealTimeLogs> {

    List<ReatimLogRes> getReatimeLogsType(@Param("orderNo") String orderNo, @Param("lineNo") String lineNo, @Param("logType") int logType, @Param("deviceType") String deviceType, @Param("size") int size);

    void delJobExecutionLog(@Param("delDay") Timestamp delDay);

    void delJobStatusTraceLog(@Param("delDay") Timestamp delDay);
}
