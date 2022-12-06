package com.dzics.sanymom.framework;

import com.alibaba.fastjson.JSON;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.model.request.agv.AutomaticGuidedVehicle;
import com.dzics.sanymom.model.request.sany.IssueOrderInformation;
import com.dzics.sanymom.model.request.syncuser.SyncMomUser;
import com.dzics.sanymom.service.SanyLogPromptMsgService;
import com.dzics.sanymom.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class LogAopAspectRepostWork {
    @Autowired
    private SanyLogPromptMsgService logPromptMsgService;
    @Value("${order.code}")
    private String orderCodeSys;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.dzics.sanymom.framework.OperLogReportWork)")
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
        OperLogReportWork opLog = method.getAnnotation(OperLogReportWork.class);
        Object[] args = joinPoint.getArgs();
        Object arg = args[0];
        if (arg instanceof WorkReportDto){
            tinvokCoreLog.setReqType("DZDC");
            tinvokCoreLog.setDetails("DZICS: " + opLog.operDesc());
            WorkReportDto reportDto = (WorkReportDto) arg;
            tinvokCoreLog.setGroupId(reportDto.getGroupId());
            tinvokCoreLog.setOrderNo(reportDto.getOrderNo());
            tinvokCoreLog.setLineNo(reportDto.getLineNo());
        }
        if (arg instanceof SearchDzdcMomSeqenceNo){
            tinvokCoreLog.setReqType("DZDC");
            tinvokCoreLog.setDetails("DZICS: " + opLog.operDesc());
            SearchDzdcMomSeqenceNo seqenceNo = (SearchDzdcMomSeqenceNo) arg;
            tinvokCoreLog.setGroupId(seqenceNo.getGroupId());
            tinvokCoreLog.setOrderNo(seqenceNo.getOrderCode());
            tinvokCoreLog.setLineNo(seqenceNo.getLineNo());
            tinvokCoreLog.setWipOrderNo(seqenceNo.getWipOrderNo());
        }
        if (arg instanceof AutomaticGuidedVehicle || arg instanceof SyncMomUser || arg instanceof IssueOrderInformation){
            tinvokCoreLog.setReqType("MOM");
            tinvokCoreLog.setDetails("MOM: " + opLog.operDesc());
            tinvokCoreLog.setGroupId("MOM");
            tinvokCoreLog.setOrderNo(orderCodeSys);
            tinvokCoreLog.setLineNo("1");
            if (arg instanceof  IssueOrderInformation){
                IssueOrderInformation information = (IssueOrderInformation) arg;
                tinvokCoreLog.setWipOrderNo(information.getTask().getWipOrderNo());
            }
        }
        tinvokCoreLog.setBrief(opLog.operModul());
        tinvokCoreLog.setDetails(opLog.operDesc());

        tinvokCoreLog.setInvokParm(JSON.toJSONString(arg));
        if (returnObj instanceof MomOrderCompleted){
            MomOrderCompleted completed = (MomOrderCompleted) returnObj;
            tinvokCoreLog.setWipOrderNo(completed.getWipOrderNoReportWork());
        }
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
