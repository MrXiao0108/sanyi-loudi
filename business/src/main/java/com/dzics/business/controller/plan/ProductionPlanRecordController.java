package com.dzics.business.controller.plan;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessProductionPlanDayService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.plan.SelectProductionPlanRecordVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.PlanRecordDetailsListDo;
import com.dzics.common.model.response.plan.SelectProductionPlanRecordDo;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 产线日生产计划记录
 * @author jq
 * Date 2021/2/19
 */
@Api(tags = {"产线日生产计划记录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/record")
@Controller
public class ProductionPlanRecordController {

    @Autowired
    BusinessProductionPlanDayService businessProductionPlanDayService;
    @OperLog(operModul = "产线日生产计划记录", operType = OperType.QUERY, operDesc = "查询日生产计划记录列表", operatorType = "后台")
    @ApiOperation(value = "查询日生产计划记录列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping
    public Result<SelectProductionPlanRecordDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                     PageLimit pageLimit, SelectProductionPlanRecordVo selectProductionPlanRecordVo){
        Result result =businessProductionPlanDayService.list(sub,pageLimit,selectProductionPlanRecordVo);
        return result;
    }

    @OperLog(operModul = "产线日生产计划记录", operType = OperType.QUERY, operDesc = "查询日生产计划记录详情列表", operatorType = "后台")
    @ApiOperation(value = "查询日生产计划记录详情列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/detailsList")
    public Result<PlanRecordDetailsListDo> detailsList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                       @RequestParam("planId" ) @ApiParam("计划id") Long planId,
                                                       @RequestParam("detectorTime") @ApiParam("生产日期")String detectorTime
                    ){
        Result result =businessProductionPlanDayService.detailsList(planId,detectorTime);
        return result;
    }
}
