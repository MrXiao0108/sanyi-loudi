package com.dzics.data.acquisition.config.task;

import com.dzics.common.service.IotSendDataService;
import com.dzics.common.service.SysCommunicationLogService;
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
public class SysLoghourTask implements SimpleJob {
    @Autowired
    private SysCommunicationLogService busCommunicationLogService;
    @Autowired
    private IotSendDataService iotSendDataService;


    @Value("${del.communication.log.day}")
    private Integer delCommunicationLog;

    @Value("${del.product.position.log.day}")
    private Integer delPostionLog;

    @Value("${del.database.log.day}")
    private Integer delIOTsLog;




    /**
     * 每小时执行一次
     */
    public void delCommunicationLog() {
        //        清理Iot日志
        try {
            log.info("开始 清理Iot日志。。。。。。。。");
            iotSendDataService.delDateBaseIot(delIOTsLog);
            log.info("结束 清理Iot日志。。。。。。。。");
        } catch (Throwable e) {
            log.error("清理Iot日志异常", e);
        }

        try {
//        清理通讯日志
            log.info("开始 清除指令日志。。。。。。。。");
            busCommunicationLogService.delCommunicationLog(delCommunicationLog,delPostionLog);
            log.info("结束 清除指令日志。。。。。。。。");
        } catch (Throwable e) {
            log.error("清除指令日志异常", e);
        }
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("开始 每小时执行清除日志操作。。。。。。。。");
        delCommunicationLog();
        log.info("结束 每小时执行清除日志操作。。。。。。。。");
    }
}
