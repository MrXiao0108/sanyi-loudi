package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.enums.EquiTypeEnum;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.constant.LogClientType;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.LogPromptMsgMomService;
import com.dzics.common.service.LogPromptMsgService;
import com.dzics.sanymom.service.SanyLogPromptMsgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

/**
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
@Slf4j
@Service
public class SanyLogPromptMsgServiceImpl implements SanyLogPromptMsgService {
    @Autowired
    private RestTemplate restTemplate;

    @Value(("${accq.read.cmd.queue.equipment.realTime}"))
    private String logQuery;

    @Value("${business.robot.ip}")
    private String busIpPort;
    @Value("${business.robot.log.path}")
    private String logPath;

    @Autowired
    private MomLogPromptMsgImpl logPromptMsgService;
    @Autowired
    private LogPromptMsgMomService logPromptMsgMomService;

    @Override
    public void saveLogPromptMsg(LogPromptMsg tinvokCoreLog) {
        try {
            LogPromptMsg logPromptMsg = logPromptMsgService.getBtGroupId(tinvokCoreLog.getGroupId());
            if (logPromptMsg != null) {
                if ("Y".equals(logPromptMsg.getInvokStatus())){
                    logPromptMsg.setHandle(1);
                }else {
                    logPromptMsg.setErrorsNums(logPromptMsg.getErrorsNums() + 1);
                }
                log.info("二次处理结果日志:{}", JSONObject.toJSONString(tinvokCoreLog));
                logPromptMsgService.updateById(logPromptMsg);
            } else {
                logPromptMsgService.saveLogPromptMsg(tinvokCoreLog);
            }
            String invokStatus = tinvokCoreLog.getInvokStatus();
            String msg = tinvokCoreLog.getBrief() + invokStatus;
            String pointCode = tinvokCoreLog.getPointCode();
            if (!StringUtils.isEmpty(pointCode)) {
                msg = msg + ":" + pointCode;
            }
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(tinvokCoreLog.getOrderNo());
            timeLogs.setLineNo(tinvokCoreLog.getLineNo());
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode("DZICS");
            timeLogs.setMessageType(1);
            timeLogs.setMessage(msg);
            timeLogs.setTimestampTime(new Date());
            //     发送到日志队列
            try {
                String url = busIpPort + logPath;
                ResponseEntity<Result> entity = restTemplate.postForEntity(url, timeLogs, Result.class);
                log.debug("发送日志完成：{}", JSONObject.toJSONString(entity));
            } catch (Throwable throwable) {
                log.error("发送日志 timeLogs :{}到业务端异常：{}", timeLogs, throwable.getMessage(), throwable);
            }
        } catch (Throwable throwable) {
            log.error("发送日志到看板异常：{}", throwable.getMessage(), throwable);
        }

    }

    @Override
    public void saveLogPromptMsgMom(LogPromptMsgMom tinvokCoreLog) {
        try {
            logPromptMsgMomService.saveLogPromptMsgMom(tinvokCoreLog);
            String invokStatus = tinvokCoreLog.getInvokStatus();
            String msg = tinvokCoreLog.getBrief() + invokStatus;
            String pointCode = tinvokCoreLog.getPointCode();
            if (!StringUtils.isEmpty(pointCode)) {
                msg = msg + ":" + pointCode;
            }
            SysRealTimeLogs timeLogs = new SysRealTimeLogs();
            timeLogs.setQueueName(logQuery);
            timeLogs.setClientId(LogClientType.BUS_AGV);
            timeLogs.setOrderCode(tinvokCoreLog.getOrderNo());
            timeLogs.setLineNo(tinvokCoreLog.getLineNo());
            timeLogs.setDeviceType(String.valueOf(EquiTypeEnum.AVG.getCode()));
            timeLogs.setDeviceCode("DZICS");
            timeLogs.setMessageType(1);
            timeLogs.setMessage(msg);
            timeLogs.setTimestampTime(new Date());
            //     发送到日志队列
            try {
                String url = busIpPort + logPath;
                ResponseEntity<Result> entity = restTemplate.postForEntity(url, timeLogs, Result.class);
                log.debug("发送日志完成：{}", JSONObject.toJSONString(entity));
            } catch (Throwable throwable) {
                log.error("发送日志 timeLogs :{}到业务端异常：{}", timeLogs, throwable.getMessage(), throwable);
            }
        } catch (Throwable throwable) {
            log.error("发送日志到看板异常：{}", throwable.getMessage(), throwable);
        }

    }
}
