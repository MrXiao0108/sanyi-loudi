package com.dzics.sanymom.framework;

import com.alibaba.fastjson.JSON;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.custom.StartWokeOrderMooM;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.util.RedisKey;
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
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author wu
 * @date 2019/03/21
 */
@Aspect
@Component
@Slf4j
public class LogAopAspect {
    @Autowired
    private SanyLogPromptMsgService logPromptMsgService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.dzics.sanymom.framework.OperLogCallAgv)")
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
        try {
            Date endDate = new Date();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            LogPromptMsg tinvokCoreLog = new LogPromptMsg();
            tinvokCoreLog.setOrgCode("SANY_MOM");
            tinvokCoreLog.setCreateDate(LocalDate.now());
            OperLogCallAgv opLog = method.getAnnotation(OperLogCallAgv.class);
            Object[] args = joinPoint.getArgs();
            EmptyFrameMovesDzdc arg = (EmptyFrameMovesDzdc) args[0];
            StartWokeOrderMooM wokeOrderMooM = arg.getWokeOrderMooM();
            if (wokeOrderMooM != null) {
                tinvokCoreLog.setCallMaterialProTaskOrderId(wokeOrderMooM.getProTaskId());
                tinvokCoreLog.setCallMaterialWipOrderNo(wokeOrderMooM.getWipOrderNo());
            }
            tinvokCoreLog.setProTaskOrderId(arg.getProTaskOrderId());
            tinvokCoreLog.setWipOrderNo(arg.getWiporderno());
            String externalCode = arg.getExternalCode();
            if (!StringUtils.isEmpty(externalCode)) {
                updatePointStatus(arg.getExternalCode(), opLog.operDesc());
            }
            tinvokCoreLog.setBrief(opLog.operDesc());
            tinvokCoreLog.setReqType(arg.getDeviceType());
            tinvokCoreLog.setDetails(arg.getDeviceType() + ": " + opLog.operDesc());
            tinvokCoreLog.setInvokPointModel(arg.getPointModel());
            tinvokCoreLog.setPointCode(externalCode);
            tinvokCoreLog.setGroupId(arg.getGroupId());
            tinvokCoreLog.setOrderNo(arg.getOrderCode());
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
        } catch (Throwable e) {
            log.error("处理日志错误 LogPromptMsg ：{}", e.getMessage(), e);
        }
    }

    private String getParam(Object[] args) {
        StringBuffer params = new StringBuffer();
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                params.append(args[i].getClass()).append(":").append(JSON.toJSONString(args[i])).append(";");
            }
        }
        return params.toString();
    }

    private void updatePointStatus(String sourceno, String pointStatus) {
        try {
            if (!StringUtils.isEmpty(sourceno)) {
                redisUtil.set(RedisKey.MATERIAL_POINT_STATUS + sourceno, pointStatus);
            }
        } catch (Throwable throwable) {
            log.error("更新料点状态错误:{}", throwable.getMessage(), throwable);
        }
    }
}
