package com.dzics.business.controller.equipment;


import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.common.model.response.EquipmentAlarmVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.equipment.EquipmentAlarmDo;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@Api(tags = {"设备告警历史"}, produces = "设备告警历史相关接口")
@RestController
@RequestMapping("/equipmentAlarm")
public class EquipmentAlarmController {

    @Autowired
    private BusinessEquipmentService equipmentAlarmAnalysisService;
    @ApiOperation(value = "设备告警列表")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping
    public Result<EquipmentAlarmDo> list(PageLimit pageLimit, EquipmentAlarmVo equipmentAlarmVo,
                                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result=equipmentAlarmAnalysisService.listAlarm(sub, pageLimit, equipmentAlarmVo);
        return result;
    }



}
