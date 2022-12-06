package com.dzics.sanymom.framework;

import com.alibaba.fastjson.JSON;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.dzics.sanymom.service.SanyLogPromptMsgService;
import com.dzics.sanymom.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author wu
 * @date 2019/03/21
 */
@Aspect
@Component
@Slf4j
public class LogAopAspectCheck {
    @Autowired
    private SanyLogPromptMsgService logPromptMsgService;


    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.dzics.sanymom.framework.OperLogReportCheck)")
    public void operLogPoinCut() {
    }

    @Around("operLogPoinCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnObj = null;
        Date startDate = new Date();
        try {
            returnObj = joinPoint.proceed();
            this.saveTinvokCoreLog(joinPoint, returnObj, startDate, "Y");
            return returnObj;
        } catch (Throwable throwable) {
            returnObj = throwable.getMessage();
            this.saveTinvokCoreLog(joinPoint, returnObj, startDate, "N");
            throw throwable;
        }
    }


    public void saveTinvokCoreLog(ProceedingJoinPoint joinPoint, Object returnObj, Date startDate, String invokStatus) {
        Date endDate = new Date();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogPromptMsg tinvokCoreLog = new LogPromptMsg();
        tinvokCoreLog.setOrgCode("SANY_MOM");
        tinvokCoreLog.setCreateDate(LocalDate.now());
        OperLogReportCheck opLog = method.getAnnotation(OperLogReportCheck.class);
        Object[] args = joinPoint.getArgs();
        MomOrderCompleted arg = ( MomOrderCompleted) args[0];
        tinvokCoreLog.setBrief(opLog.operModul());
        tinvokCoreLog.setDetails(opLog.operDesc());
        tinvokCoreLog.setWipOrderNo(arg.getWipOrderNo());
        tinvokCoreLog.setProTaskOrderId(arg.getProTaskId());
        tinvokCoreLog.setReqType("DZDC");
        tinvokCoreLog.setDetails("DZICS: " + opLog.operDesc());
        tinvokCoreLog.setGroupId(arg.getGroupId());
        tinvokCoreLog.setOrderNo(arg.getOrderNo());
        tinvokCoreLog.setLineNo(arg.getLineNo());
        tinvokCoreLog.setInvokParm(JSON.toJSONString(arg));
        tinvokCoreLog.setInvokMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
        tinvokCoreLog.setInvokReturn(returnObj.getClass() != null ? returnObj.getClass().getName() + ":" + JSON.toJSONString(returnObj) : "");
        tinvokCoreLog.setStartTime(startDate);
        tinvokCoreLog.setEndTime(endDate);
        tinvokCoreLog.setInvokCost(new BigDecimal((endDate.getTime() - startDate.getTime())));
        tinvokCoreLog.setInvokStatus(invokStatus);
        tinvokCoreLog.setErrorsNums("Y".equals(invokStatus) ? 0 : 1);
        tinvokCoreLog.setGrade("Y".equals(invokStatus) ? 0 : 5);
        logPromptMsgService.saveLogPromptMsg(tinvokCoreLog);
    }

}
