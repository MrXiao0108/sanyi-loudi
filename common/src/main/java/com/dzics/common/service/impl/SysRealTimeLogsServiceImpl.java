package com.dzics.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dzics.common.dao.SysRealTimeLogsMapper;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.response.ReatimLogRes;
import com.dzics.common.service.SysRealTimeLogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 设备运行告警日志 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Service
public class SysRealTimeLogsServiceImpl extends ServiceImpl<SysRealTimeLogsMapper, SysRealTimeLogs> implements SysRealTimeLogsService {
    @Autowired
    private SysRealTimeLogsMapper logsMapper;

    @Override
    public List<ReatimLogRes> getReatimeLogsType(String orderNo, String lineNo, int logType, String deviceType, int size) {
        return logsMapper.getReatimeLogsType(orderNo, lineNo, logType, deviceType,size);
    }

    @Override
    public void sysDelRealday(Integer days) {
        long time = System.currentTimeMillis() - ((long) days * (24 * 3600 * 1000));
        Date date = new Date(time);
        QueryWrapper<SysRealTimeLogs> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("timestamp_time", date);
        logsMapper.delete(queryWrapper);
    }

    @Override
    public void delJobExecutionLog(int delDay) {
        long time = System.currentTimeMillis() - ((long) delDay * (24 * 3600 * 1000));
        Timestamp timestamp = new Timestamp(time);
        logsMapper.delJobExecutionLog(timestamp);
    }

    @Override
    public void delJobStatusTraceLog(int delDay) {
        long time = System.currentTimeMillis() - ((long) delDay * (24 * 3600 * 1000));
        Timestamp timestamp = new Timestamp(time);
        logsMapper.delJobStatusTraceLog(timestamp);
    }
}
