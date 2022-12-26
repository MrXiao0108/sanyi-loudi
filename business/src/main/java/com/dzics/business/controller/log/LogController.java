package com.dzics.business.controller.log;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.CommuLogPrm;
import com.dzics.business.service.BusCommunicationLogService;
import com.dzics.business.service.BusiLoginLogService;
import com.dzics.business.service.BusiOperationLog;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.SysLoginLog;
import com.dzics.common.model.entity.SysOperationLogging;
import com.dzics.common.model.request.SysOperationLoggingVo;
import com.dzics.common.model.request.SysloginVo;
import com.dzics.common.model.request.mom.BackMomLogVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.LogPromptMsgMomService;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 日志相关api接口
 *
 * @author ZhangChengJun
 * Date 2021/1/15.
 * @since
 */
@Api(tags = {"日志管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user")
@Controller
public class LogController {
    @Autowired
    private BusCommunicationLogService busCommunicationLogService;

    @Autowired
    private BusiOperationLog busiOperationLog;
    @Autowired
    private BusiLoginLogService loginLogService;
    @Autowired
    private LogPromptMsgMomService logPromptMsgMomService;

    @OperLog(operModul = "日志管理", operType = OperType.QUERY, operDesc = "操作日志", operatorType = "后台")
    @ApiOperation(value = "操作日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/log")
    public Result<SysOperationLogging> queryOperLog(
            PageLimit pageLimit, SysOperationLoggingVo sysOperationLoggingVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
            @RequestHeader(value = "code", required = false) @ApiParam(value = "用户账号", required = true) String code) {
        return busiOperationLog.queryOperLog(pageLimit, sub, code, sysOperationLoggingVo);
    }

    @OperLog(operModul = "日志管理", operType = OperType.QUERY, operDesc = "登录日志", operatorType = "后台")
    @ApiOperation(value = "登录日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/log/login")
    public Result<SysLoginLog> queryLogin(
            PageLimit pageLimit, SysloginVo sysloginVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
            @RequestHeader(value = "code", required = false) @ApiParam(value = "用户账号", required = true) String code) {
        return loginLogService.queryLogin(pageLimit, sysloginVo, sub, code);
    }

    @ApiOperation(value = "通信日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/communication/log")
    public Result communicationLog(PageLimit pageLimit, CommuLogPrm commuLogPrm,
                                   @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busCommunicationLogService.communicationLog(pageLimit, commuLogPrm);
    }

    @ApiOperation(value = "通信日志指令", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/communication/tcp/log")
    public Result communicationLogTcp(PageLimit pageLimit, CommuLogPrm commuLogPrm,
                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busCommunicationLogService.communicationLogTcp(pageLimit, commuLogPrm);
    }

    @ApiOperationSupport(author = "xnb", order = 1)
    @ApiOperation(value = "MOM日志查询", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/back/logs")
    public Result getBackMomLogs(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                 @Valid BackMomLogVo backMomLogVo) {
        return logPromptMsgMomService.getBackMomLogs(backMomLogVo);
    }

}
