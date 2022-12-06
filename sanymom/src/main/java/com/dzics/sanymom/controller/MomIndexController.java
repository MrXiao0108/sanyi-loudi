package com.dzics.sanymom.controller;

import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.entity.LogPromptMsgMom;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetWorkDateDo;
import com.dzics.common.model.response.mom.MaterialPointStatus;
import com.dzics.common.model.response.mom.MomAuthOrderRes;
import com.dzics.common.service.DzWorkingFlowService;
import com.dzics.common.util.PageLimitBase;
import com.dzics.sanymom.model.request.AgvModel;
import com.dzics.sanymom.model.request.ErrorDetailsparms;
import com.dzics.sanymom.model.request.MomGroupId;
import com.dzics.sanymom.model.request.OpenWork;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.MomUserMessage;
import com.dzics.sanymom.service.impl.AgvLogServiceImpl;
import com.dzics.sanymom.service.impl.MomLogPromptMsgImpl;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

/**
 * @author ZhangChengJun
 * Date 2022/1/22.
 * @since
 */
@Api(tags = {"首页接口"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/mom/user")
@Slf4j
@CrossOrigin
public class MomIndexController {
    @Autowired
    private MomUserMessage momUserService;
    @Autowired
    private AgvLogServiceImpl agvLogService;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomLogPromptMsgImpl momLogPromptMsg;
    @Autowired
    private DzWorkingFlowService workingFlowService;

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "料点状态", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/material/point")
    public Result<MaterialPointStatus> getMaterialPointStatus() {
        return momUserService.getMaterialPointStatus();
    }


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "订单列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/order")
    public Result<MomAuthOrderRes> getMomAuthOrderRes(PageLimitBase pageLimitBase) {
        return momUserService.getMomAuthOrderRes(pageLimitBase);
    }


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "查看上传", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/off/no/open/work")
    public Result offNoOpenWork() {
        return momUserService.offNoOpenWork();
    }


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "控制上传", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/off/no/open/close")
    public Result offNoOpen(@Valid @RequestBody OpenWork openWork) {
        return momUserService.offNoOpen(openWork);
    }


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "AGV请求日志", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/agv/logs")
    public Result<LogPromptMsg> getLogPropMsg(PageLimitBase pageLimitBase) {
        return agvLogService.getLogPropMsg(pageLimitBase);
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "错误详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/get/agv/logs/details")
    public Result<LogPromptMsgMom> getLogPropMsgMom(@Valid ErrorDetailsparms pageLimitBase) {
        return agvLogService.getLogPropMsgMom(pageLimitBase.getGroupId(), pageLimitBase);
    }


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "重新请求", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/get/agv/logs")
    public Result againRequest(@Valid @RequestBody MomGroupId logId) {
        return momLogPromptMsg.againRequest(logId.getLogId());
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "取消", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/get/agv/logs")
    public Result cancelRequest(@Valid @RequestBody MomGroupId logId) {
        return momLogPromptMsg.cancelRequest(logId.getLogId());
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "AGV呼叫模式", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/mom/run/model")
    public Result<AgvModel> agvRunModel() {
        String runModel = cachingApi.getMomRunModel();
        if (StringUtils.isEmpty(runModel)) {
            throw new CustomException(CustomExceptionType.Parameter_Exception, CustomResponseCode.ERR95);
        }
        int rm = runModel.equals("auto") ? 1 : 0;
        AgvModel agvModel = new AgvModel();
        agvModel.setRm(rm);
        return Result.OK(agvModel);
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "修改AGV呼叫模式", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping("/mom/run/model")
    public Result updateAgvRunModel(@Valid @RequestBody AgvModel logId){
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        String status = cachingApi.updateModelWhere(logId.getRm(),line);
        if("ok".equals(status)){
            String momRunModel = cachingApi.updateAgvRunModel(logId.getRm(),line);
            return Result.OK(momRunModel);
        }
        return new Result(CustomExceptionType.SYSTEM_ERROR,CustomResponseCode.ERR961.getChinese());
    }


    @ApiOperationSupport(author = "xnb",order = 1)
    @ApiOperation(value = "查询当班计划汇总数据",consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/mom/dayWorkDate")
    public Result<GetWorkDateDo> getDayWorkDate() throws ParseException {
        DzProductionLine line = cachingApi.getOrderIdAndLineId();
        Result<GetWorkDateDo> dayWorkDate = workingFlowService.getDayWorkDate(line);
        return dayWorkDate;
    }



}
