package com.dzics.business.framework.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.dto.SaveLogJson;
import com.dzics.common.dao.SysUserMapper;
import com.dzics.common.enums.OperTypeStatus;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.CustomWarnException;
import com.dzics.common.model.entity.SysOperationLogging;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysOperationLoggingService;
import com.dzics.common.util.DateUtil;
import com.dzics.common.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
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
public class LogAopAspect {

    @Autowired
    private DateUtil dateUtil;
    @Autowired
    private SysOperationLoggingService operationLogService;
    @Autowired
    private SysUserMapper userMapper;

    /**
     * 设置操作日志切入点 记录操作日志 在注解的位置切入代码
     */
    @Pointcut("@annotation(com.dzics.business.common.annotation.OperLog)")
    public void operLogPoinCut() {
    }

    /**
     * 设置操作异常切入点记录异常日志 扫描所有controller包下操作
     */
//    @Pointcut("execution(* com.dzics.business.controller..*.*(..))")
//    public void operExceptionLogPoinCut() {
//    }
    @Around("operLogPoinCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//            方法运行开始时间
        long startTimeMillis = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
//            方法运行结束时间
            long execTimeMillis = System.currentTimeMillis() - startTimeMillis;
            try {
                // 获取RequestAttributes
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                // 从获取RequestAttributes中获取HttpServletRequest的信息
                HttpServletRequest request = (HttpServletRequest) requestAttributes
                        .resolveReference(RequestAttributes.REFERENCE_REQUEST);

                SysOperationLogging operlog = new SysOperationLogging();
                // 从切面织入点处通过反射机制获取织入点处的方法
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                // 获取切入点所在的方法
                Method method = signature.getMethod();
                // 获取操作
                OperLog opLog = method.getAnnotation(OperLog.class);
                if (opLog != null) {
                    operlog.setTitle(opLog.operModul());
                    operlog.setBusinessType(opLog.operType());
                    operlog.setOperDesc(opLog.operDesc());
                    operlog.setOperatorType(opLog.operatorType());
                }
                // 获取请求的类名
                String className = joinPoint.getTarget().getClass().getName();
                // 获取请求的方法名
                String methodName = method.getName();
                methodName = className + "." + methodName;
                // 请求方法
                operlog.setMethod(methodName);
                operlog.setRequestMethod(request.getMethod());
                // 请求的参数
                Map<String, String> rtnMap = converMap(request.getParameterMap());
                // 将参数所在的数组转换成json
                String params = JSON.toJSONString(rtnMap);
                SaveLogJson saveLogJson = new SaveLogJson();
                saveLogJson.setParameterMap(params);
                if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
                    String body = "";
                    try {
                        body = JSON.toJSONString(joinPoint.getArgs());
                    } catch (Throwable e) {
                        body = joinPoint.toString();
                    }
                    saveLogJson.setBodyParms(body);
                } else {
                    saveLogJson.setBodyParms("");
                }

                operlog.setOperParam(JSON.toJSONString(saveLogJson));
                String requestUrl = request.getRequestURI();
                operlog.setOperName(request.getHeader("sub"));
                operlog.setOrgCode(request.getHeader("code"));
                operlog.setOperIp(IPUtil.getIpAddress(request));
                operlog.setOperUrl(requestUrl);
                if (result instanceof Result) {
                    Result save = JSONObject.parseObject(JSON.toJSONString(result), Result.class);
                    save.setData(null);
                    operlog.setJsonResult(JSON.toJSONString(save));
                } else {
                    operlog.setJsonResult(JSON.toJSONString(result));
                }
                operlog.setOperTime(new Date());
                operlog.setStatus(OperTypeStatus.SUCCESS);
                operlog.setOperDate(LocalDate.now());
                operlog.setRunTime(execTimeMillis);
                log.debug("API接口调用日志：请求地址：{},模块：{},操作描述：{},业务：{},方法名：{},请求方式：{},操作人员：{}",
                        operlog.getOperUrl(), operlog.getTitle(), operlog.getOperDesc(), operlog.getBusinessType(), operlog.getMethod(),
                        operlog.getRequestMethod(), operlog.getOperName());
                log.debug("API接口调用日志：请求地址：{},主机地址：{},操作地点：{},操作状态：{},异常信息：{}，异常名称：{}，操作时间：{}"
                        , operlog.getOperUrl(), operlog.getOperIp(), operlog.getOperLocation(), operlog.getStatus(), operlog.getExcMessage(),
                        operlog.getErrorMsg(), dateUtil.dateFormatToStingYmdHms(operlog.getOperTime()));
                log.debug("API接口调用日志：请求地址：{},请求参数：{}", operlog.getOperUrl(), operlog.getOperParam());
                if (!operlog.getOperUrl().equals("/api/user/auth/getInfo")) {
                    log.debug("API接口调用日志：请求地址：{},返回参数：{}", operlog.getOperUrl(), operlog.getJsonResult());
                }
                log.debug("API接口调用日志: 请求地址：{},执行时间：{}", operlog.getOperUrl(), execTimeMillis);
                if (operlog.getOrgCode() == null) {
                    operlog.setOrgCode("unknown");
                }
                operationLogService.save(operlog);
            } catch (Throwable e) {
                if (e instanceof CustomWarnException) {
                    log.warn("记录操作日志异常：errMsg {}", e.getMessage());
                } else {
                    log.error("记录操作日志异常：errMsg {}", e);
                }
            }
            return result;
        } catch (Throwable e) {
            //            方法运行结束时间
            long execTimeMillis = System.currentTimeMillis() - startTimeMillis;
            try {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                // 从获取RequestAttributes中获取HttpServletRequest的信息
                HttpServletRequest request = (HttpServletRequest) requestAttributes
                        .resolveReference(RequestAttributes.REFERENCE_REQUEST);

                SysOperationLogging excepLog = new SysOperationLogging();
                // 从切面织入点处通过反射机制获取织入点处的方法
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                // 获取切入点所在的方法
                Method method = signature.getMethod();
                // 获取操作
                OperLog opLog = method.getAnnotation(OperLog.class);
                if (opLog != null) {
                    excepLog.setTitle(opLog.operModul());
                    excepLog.setBusinessType(opLog.operType());
                    excepLog.setOperDesc(opLog.operDesc());
                    excepLog.setOperatorType(opLog.operatorType());
                }
                // 获取请求的类名
                String className = joinPoint.getTarget().getClass().getName();
                // 获取请求的方法名
                String methodName = method.getName();
                methodName = className + "." + methodName;
                // 请求的参数
                Map<String, String> rtnMap = converMap(request.getParameterMap());
                // 将参数所在的数组转换成json
                String params = JSON.toJSONString(rtnMap);
                SaveLogJson saveLogJson = new SaveLogJson();
                saveLogJson.setParameterMap(params);
                saveLogJson.setBodyParms((joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) ? JSON.toJSONString(joinPoint.getArgs()) : "");
                excepLog.setOperParam(JSON.toJSONString(saveLogJson));
                excepLog.setRequestMethod(request.getMethod());
                excepLog.setMethod(methodName);
                excepLog.setErrorMsg(e.getClass().getName());
                if (e instanceof CustomWarnException) {
                    CustomWarnException cust = (CustomWarnException) e;
                    excepLog.setExcMessage(cust.getClass().getName() + "Msg:->" + cust.getMessage() + "Code:->" + cust.getCode());
                }
                if (e instanceof CustomException) {
                    CustomException cust = (CustomException) e;
                    excepLog.setExcMessage(cust.getClass().getName() + "Msg:->" + cust.getMessage() + "Code:->" + cust.getCode());
                } else {
                    excepLog.setExcMessage(stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace()));
                }
                excepLog.setOperName(request.getHeader("sub"));
                excepLog.setOperUrl(request.getRequestURI());
                excepLog.setOperIp(IPUtil.getIpAddress(request));
                excepLog.setOperTime(new Date());
                excepLog.setStatus(OperTypeStatus.ERROR);
                excepLog.setOperDate(LocalDate.now());
                excepLog.setRunTime(execTimeMillis);
                QueryWrapper<SysUser> q = new QueryWrapper();
                String sub = request.getHeader("sub");
                q.eq("username", sub);
                SysUser sysUser = userMapper.selectOne(q);
                if (sysUser == null) {
                    log.warn("request.getHeader(sub):username:{},没有查询到用户", sub);
                    excepLog.setOrgCode("ERROR");
                } else {
                    excepLog.setOrgCode(sysUser.getOrgCode());
                }
                log.warn("API接口调用日志：请求地址：{},模块：{},操作描述：{},业务：{},方法名：{},请求方式：{},操作人员：{}",
                        excepLog.getOperUrl(), excepLog.getTitle(), excepLog.getOperDesc(), excepLog.getBusinessType(), excepLog.getMethod(),
                        excepLog.getRequestMethod(), excepLog.getOperName());
                log.warn("API接口调用日志：请求地址：{},主机地址：{},操作地点：{},操作状态：{},异常信息：{}，异常名称：{}，操作时间：{}"
                        , excepLog.getOperUrl(), excepLog.getOperIp(), excepLog.getOperLocation(), excepLog.getStatus(), excepLog.getExcMessage(),
                        excepLog.getErrorMsg(), dateUtil.dateFormatToStingYmdHms(excepLog.getOperTime()));
                log.warn("API接口调用日志：请求地址：{},请求参数：{}", excepLog.getOperUrl(), excepLog.getOperParam());
                log.warn("API接口调用日志：请求地址：{},,返回参数：{}", excepLog.getOperUrl(), excepLog.getJsonResult());
                log.warn("API接口调用日志: 请求地址：{},执行时间：{}", excepLog.getOperUrl(), execTimeMillis);
                operationLogService.save(excepLog);
            } catch (Throwable e2) {
                if (e2 instanceof CustomWarnException) {
                    log.warn("记录操作日志异常：errMsg {}", e2.getMessage());
                } else {
                    log.error("记录操作日志异常：errMsg {}", e2);
                }
            }
            throw e;
        }
    }

    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */
    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }

    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }
}
