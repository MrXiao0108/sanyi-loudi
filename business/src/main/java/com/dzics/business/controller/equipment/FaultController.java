package com.dzics.business.controller.equipment;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusDzRepairHistoryService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.device.AddFaultRecordParms;
import com.dzics.common.model.request.device.FaultRecordParmsDateils;
import com.dzics.common.model.request.device.FaultRecordParmsReq;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.FaultRecordDetails;
import com.dzics.common.util.PageLimitBase;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2021/9/28.
 * @since
 */
@Api(tags = {"故障记录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/fault/record")
@Controller
public class FaultController {
    @Autowired
    private BusDzRepairHistoryService dzRepairHistoryService;

    @ApiOperation(value = "故障记录列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 135)
    @GetMapping
    public Result<FaultRecord> getFaultRecordList(PageLimitBase pageLimit, FaultRecordParmsReq parmsReq,
                                                  @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzRepairHistoryService.getFaultRecordList(sub, pageLimit, parmsReq);
    }

    @ApiOperation(value = "故障记录详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 136)
    @GetMapping("/details")
    public Result<FaultRecordDetails> getFaultRecordDetails(@Valid FaultRecordParmsDateils parmsReq,
                                                            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzRepairHistoryService.getFaultRecordDetails(sub, parmsReq);
    }

    @OperLog(operModul = "故障记录", operType = OperType.ADD, operDesc = "新增", operatorType = "后台")
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 137)
    @PostMapping
    public Result addFaultRecord(@RequestBody @Valid AddFaultRecordParms parmsReq,
                                 @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzRepairHistoryService.addFaultRecord(sub, parmsReq);
    }

    @OperLog(operModul = "故障记录", operType = OperType.UPDATE, operDesc = "编辑", operatorType = "后台")
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 137)
    @PutMapping
    public Result updateFaultRecord(@RequestBody @Valid AddFaultRecordParms parmsReq,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzRepairHistoryService.updateFaultRecord(sub, parmsReq);
    }

    @OperLog(operModul = "故障记录", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 137)
    @DeleteMapping("/{repairId}")
    public Result delFaultRecord(  @PathVariable("repairId") @ApiParam(name = "维修记录ID必填", required = true) String repairId,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzRepairHistoryService.delFaultRecord(sub, repairId);
    }

}
