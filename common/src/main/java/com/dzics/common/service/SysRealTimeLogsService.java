package com.dzics.common.service;

import com.dzics.common.model.entity.SysRealTimeLogs;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.response.ReatimLogRes;

import java.util.List;

/**
 * <p>
 * 设备运行告警日志 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
public interface SysRealTimeLogsService extends IService<SysRealTimeLogs> {

    List<ReatimLogRes> getReatimeLogsType(String orderNo, String lineNo, int logType, String deviceType, int size);

    void sysDelRealday(Integer delRealday);

    void delJobExecutionLog(int i);

    void delJobStatusTraceLog(int i);
}
