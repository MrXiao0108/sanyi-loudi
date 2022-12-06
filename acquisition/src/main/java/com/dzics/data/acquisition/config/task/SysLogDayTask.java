package com.dzics.data.acquisition.config.task;

import com.dzics.common.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.elasticjob.api.ShardingContext;
import org.apache.shardingsphere.elasticjob.simple.job.SimpleJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 系统日志清理任务
 *
 * @author ZhangChengJun
 * Date 2021/3/3.
 * @since
 */
@Service
@Slf4j
public class SysLogDayTask implements SimpleJob {
    @Autowired
    private SysOperationLoggingService operationLoggingService;
    @Autowired
    private SysLoginLogService loginLogService;
    @Autowired
    private DzEquipmentStateLogService businessEquipmentStateLogService;
    @Autowired
    private SysRealTimeLogsService sysRealTimeLogsService;
    @Autowired
    private DzEquipmentProNumDetailsService detailsService;

    @Value("${del.sys.real.time.logs.day}")
    private Integer delRealday;

    @Value("${del.operation.log.day}")
    private Integer delOperationLog;

    @Value("${del.login.log.day}")
    private Integer delLoginLog;

    @Value("${del.equipment.log.day}")
    private Integer delEquipmentLog;

    /**
     * 清理系统操作日志
     */
    public void delOperationLog() {

        try {
            //清理系统操作日志
            log.info("开始 清理系统操作日志。。。。。。。。");
            operationLoggingService.delOperationLog(delOperationLog);
            log.info("结束 清理系统操作日志。。。。。。。。");
        } catch (Throwable throwable) {
            log.error("清理系统操作日志异常", throwable);
        }
        try {
            //清理系统登录日志.
            log.info("开始 清理系统登录日志。。。。。。。。");
            loginLogService.delLoginLog(delLoginLog);
            log.info("结束 清理系统登录日志。。。。。。。。");
        } catch (Throwable throwable) {
            log.error("清理系统登录日志异常", throwable);
        }

        try {
            //清理设备运行日志
            log.info("开始 清理设备运行日志。。。。。。。。");
            businessEquipmentStateLogService.delEquimentLog(delEquipmentLog);
            log.info("结束 清理设备运行日志。。。。。。。。");
        } catch (Throwable throwable) {
            log.error("清理设备运行日志异常", throwable);
        }

        try {
            //清理设备告警日志
            log.info("开始 删除设备告警日志 sys_real_time_logs ");
            sysRealTimeLogsService.sysDelRealday(delRealday);
            log.info("结束 删除设备告警日志 sys_real_time_logs ");
        } catch (Throwable e) {
            log.error("删除设备告警日志异常", e);
        }


        try {
            log.info("开始 删除任务运行日志 job_execution_log ");
            sysRealTimeLogsService.delJobExecutionLog(3);
            log.info("结束 删除任务运行日志 job_execution_log ");
        } catch (Throwable e) {
            log.error("删除任务运行日志异常", e);
        }

        try {
            log.info("开始 删除任务运行日志 job_status_trace_log ");
            sysRealTimeLogsService.delJobStatusTraceLog(3);
            log.info("结束 删除任务运行日志 job_status_trace_log ");
        } catch (Throwable e) {
            log.error("删除任务运行日志异常", e);
        }

        try {
            log.info("开始 删除设备生产数量详情表20天之前的记录 dz_equipment_pro_num_details ");
            detailsService.delProNumDetails(7);
            log.info("结束 删除设备生产数量详情表20天之前的记录 dz_equipment_pro_num_details ");
        } catch (Throwable e) {
            log.error("删除设备生产数量详情表20天之前的记录异常", e);
        }
    }


    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始 每天执行清除日志操作。。。。。。。。");
        delOperationLog();
        log.info("结束 每天执行清除日志操作。。。。。。。。");
    }
}
