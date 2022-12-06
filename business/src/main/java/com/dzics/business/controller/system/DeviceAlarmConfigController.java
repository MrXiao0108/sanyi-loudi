package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.alarm.AddDeviceAlarmConfig;
import com.dzics.business.model.vo.alarm.GetDeivceAlarmConfig;
import com.dzics.business.service.BusDeviceAlarmConfigService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzDeviceAlarmConfig;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 告警字典配置接口
 *
 * @author ZhangChengJun
 * Date 2021/6/21.
 * @since
 */
@Api(tags = {"设备告警配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/alarm/config/device")
public class DeviceAlarmConfigController {

    @Autowired
    private BusDeviceAlarmConfigService deviceAlarmConfig;

    @OperLog(operModul = "设备告警配置", operType = OperType.ADD, operDesc = "新增", operatorType = "后台")
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping
    public Result addGiveAlarmConfig(
            @Valid @RequestBody AddDeviceAlarmConfig alarmConfig,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return deviceAlarmConfig.addGiveAlarmConfig(alarmConfig, sub);
    }

    @OperLog(operModul = "设备告警配置", operType = OperType.UPDATE, operDesc = "编辑", operatorType = "后台")
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping
    public Result putGiveAlarmConfig(
            @Valid @RequestBody AddDeviceAlarmConfig alarmConfig,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return deviceAlarmConfig.putGiveAlarmConfig(alarmConfig, sub);
    }

    @ApiOperation(value = "查询", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping
    public Result<DzDeviceAlarmConfig> getGiveAlarmConfig(
            GetDeivceAlarmConfig alarmConfig,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return deviceAlarmConfig.getGiveAlarmConfig(alarmConfig, sub);
    }



    @OperLog(operModul = "设备告警配置", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping("/{alarmConfigId}")
    public Result delGiveAlarmConfig(
            @PathVariable("alarmConfigId") @ApiParam(value = "唯一标识", required = true) String alarmConfigId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return deviceAlarmConfig.delGiveAlarmConfig(alarmConfigId, sub);
    }


}
