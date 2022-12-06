package com.dzics.business.controller.dzmom.log;

import com.dzics.common.model.dto.check.LogPromptMsgDto;
import com.dzics.common.model.request.mom.AgvLogParms;
import com.dzics.business.model.dto.ErrorDetailsparms;
import com.dzics.business.service.agv.impl.AgvLogServiceImpl;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Administrator
 */
@Api(tags = {"MOM请求日志"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/mom/logs")
@RestController
public class MomLogController {

    @Autowired
    private AgvLogServiceImpl agvLogService;


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "AGV请求日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/agv/logs")
    public Result<LogPromptMsgDto> getLogPropMsg(AgvLogParms logParms) {
        return agvLogService.getLogPropMsg(logParms);
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "错误详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/agv/logs/details")
    public Result<LogPromptMsgMom> getLogPropMsgMom(@Valid ErrorDetailsparms pageLimitBase) {
        return agvLogService.getLogPropMsgMom(pageLimitBase.getGroupId(), pageLimitBase);
    }

}
