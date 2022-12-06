package com.dzics.sanymom.framework;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.mom.response.GeneralControlModel;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.sanymom.service.SanyLogPromptMsgService;
import com.google.gson.Gson;
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
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * 切面处理类，操作日志异常日志记录处理
 *
 * @author wu
 * @date 2019/03/21
 */
@Aspect
@Component
@Slf4j
public class LogAopAspectHttp {
    @Autowired
    private SanyLogPromptMsgService logPromptMsgService;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.dzics.sanymom.framework.OperLogCallMom)")
    public void operLogPoinCut() {
    }

    @Around("operLogPoinCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object returnObj = null;
        Date startDate = new Date();
        try {
            returnObj = joinPoint.proceed();
            this.saveTinvokCoreLog(joinPoint, returnObj, startDate, "成功");
            return returnObj;
        } catch (Throwable throwable) {
            returnObj = throwable.getMessage();
            this.saveTinvokCoreLog(joinPoint, returnObj, startDate, "失败");
            throw throwable;
        }
    }


    public void saveTinvokCoreLog(ProceedingJoinPoint joinPoint, Object returnObj, Date startDate, String invokStatus) {
        try {
            Date endDate = new Date();
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperLogCallMom opLog = method.getAnnotation(OperLogCallMom.class);
            LogPromptMsgMom tinvokCoreLog = new LogPromptMsgMom();
            tinvokCoreLog.setOrgCode("SANY_MOM");
            tinvokCoreLog.setCreateDate(LocalDate.now());
            Object[] args = joinPoint.getArgs();
            tinvokCoreLog.setBrief(opLog.operModul());
            tinvokCoreLog.setDetails(opLog.operDesc());
            Gson gson = new Gson();
            String jsonString = gson.toJson(args[5]);
            JSONObject jsonObject = JSONObject.parseObject(jsonString);

            String taskId = String.valueOf(jsonObject.get("taskId"));
            if ("报工".equals(opLog.operModul())) {
                JSONArray josb = (JSONArray) jsonObject.get("reported");
                JSONObject o = (JSONObject) josb.get(0);
                String reqId = String.valueOf(o.get("reqId"));
                String wipOrderNo = String.valueOf(o.get("WipOrderNo"));
                tinvokCoreLog.setWipOrderNo(wipOrderNo);
                tinvokCoreLog.setReqId(reqId);
            } else {
                JSONObject josb = (JSONObject) jsonObject.get("reported");
                String reqId = String.valueOf(josb.get("reqId"));
                tinvokCoreLog.setReqId(reqId);
            }
            tinvokCoreLog.setTaskId(taskId);
            tinvokCoreLog.setInvokParm(jsonString);
            String innerGroupId = (String) args[0];
            String orderCode = (String) args[1];
            String lineNo = (String) args[2];
            String groupId = (String) args[3];
            tinvokCoreLog.setReqType("请求MOM-API");
            tinvokCoreLog.setOrderNo(orderCode);
            tinvokCoreLog.setLineNo(lineNo);
            tinvokCoreLog.setGroupId(groupId);
            tinvokCoreLog.setInnerGroupId(innerGroupId);
            tinvokCoreLog.setInvokMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName());
            tinvokCoreLog.setInvokReturn(returnObj != null ? JSON.toJSONString(returnObj) : "");
            tinvokCoreLog.setStartTime(startDate);
            tinvokCoreLog.setEndTime(endDate);
            BigDecimal divide = new BigDecimal((endDate.getTime() - startDate.getTime())).divide(new BigDecimal(1000), 2, RoundingMode.HALF_UP);
            tinvokCoreLog.setInvokCost(divide);
            tinvokCoreLog.setInvokStatus(invokStatus);
            tinvokCoreLog.setGrade("成功".equals(invokStatus) ? 0 : 5);
            logPromptMsgService.saveLogPromptMsgMom(tinvokCoreLog);
        } catch (Throwable e) {
            log.error("处理日志错误：{}", e.getMessage(), e);
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

}
