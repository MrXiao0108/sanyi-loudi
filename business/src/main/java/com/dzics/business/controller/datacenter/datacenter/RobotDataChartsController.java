package com.dzics.business.controller.datacenter.datacenter;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.response.LineDataAnalysisX;
import com.dzics.business.model.vo.plan.PlanAnalysisGraphical;
import com.dzics.business.model.vo.plan.PlanAnalysisGraphicalTime;
import com.dzics.business.service.BusinessEquipmentProNumDetailsService;
import com.dzics.business.service.BusinessProductionPlanService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.charts.IntelligentDetectionVo;
import com.dzics.common.model.request.charts.RobotDataChartsListVo;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@Api(tags = {"数据中心"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/charts/data/center")
public class RobotDataChartsController {
    @Autowired
    private BusinessEquipmentProNumDetailsService businessEquipmentProNumDetailsService;
    @Autowired
    private BusinessProductionPlanService businessProductionPlanService;

    @ApiOperation(value = "设备生产数据走势图")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = false) String tokenHdaer,
                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = false) String sub,
                       @Valid RobotDataChartsListVo robotDataChartsListVo) {
        Result result = businessEquipmentProNumDetailsService.list(robotDataChartsListVo);
        return result;
    }

    @ApiOperation(value = "产线计划分析", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 2)
    @GetMapping(value = "/plan")
    public Result<?> planAnalysisGraphical(@Valid PlanAnalysisGraphical graphical,
                                                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessProductionPlanService.planAnalysisGraphical(sub, graphical);
    }


    @ApiOperation(value = "设备用时分析", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 3)
    @GetMapping(value = "/equipment/time/analysis")
    public Result equipmentTime(@Valid PlanAnalysisGraphicalTime graphical,
                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {

//        return businessProductionPlanService.equipmentTimeAnalysis(sub, graphical); V1
        return businessProductionPlanService.equipmentTimeAnalysisV2(sub, graphical);
    }

    @ApiOperation(value = "智能检测系统", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 4)
    @PostMapping(value = "/intelligent/detection")
    public Result intelligentDetection( @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @RequestBody @Valid IntelligentDetectionVo intelligentDetectionVo) throws ParseException {
        return businessProductionPlanService.intelligentDetection(sub, intelligentDetectionVo);
    }


}
