package com.dzics.business.controller.datacenter.datacenter;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessEquipmentDowntimeRecordService;
import com.dzics.business.service.BusinessEquipmentProNumService;
import com.dzics.business.service.BusinessProductionPlanDayService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.ProductionVo;
import com.dzics.common.model.request.charts.ActivationVo;
import com.dzics.common.model.request.charts.RobotDataChartsListVo;
import com.dzics.common.model.response.ProductionDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.charts.ActivationDo;
import com.dzics.common.model.response.charts.OperationDo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"数据中心"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/charts/data/center")
public class EquipmentOperation {

    @Autowired
    BusinessEquipmentDowntimeRecordService businessEquipmentDowntimeRecordService;
    @Autowired
    BusinessProductionPlanDayService businessProductionPlanDayService;
    @Autowired
    BusinessEquipmentProNumService businessEquipmentProNumService;

    @ApiOperation(value = "设备运行率分析")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping("/operation")
    public Result<OperationDo> rate(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = false) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = false) String sub,
             @Valid RobotDataChartsListVo robotDataChartsListVo) {
        Result result = businessEquipmentDowntimeRecordService.operation(sub, robotDataChartsListVo);
        return result;
    }



    @ApiOperation(value = "设备稼动率分析")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping("/activation")
    public Result<ActivationDo> activation(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = false) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = false) String sub,
            @Valid ActivationVo activationVo
    ){
        Result result =businessProductionPlanDayService.activation(sub,activationVo);
        return result;
    }

    @ApiOperation(value = "设备生产分时数据")
    @ApiOperationSupport(author = "xnb", order = 5)
    @GetMapping("/productionTime")
    public Result<ProductionDo> production(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = false) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = false) String sub,
             @Valid ProductionVo productionVo){
        Result result = businessEquipmentProNumService.listProductionTime(sub, productionVo);
        return result;
    }

}
