package com.dzics.business.controller.equipment;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.PutEquipmentDataStateVo;
import com.dzics.common.model.response.EquipmentStateDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"设备状态管理"}, produces = "设备状态管理相关接口")
@RestController
@RequestMapping("/api/equipment/state")
public class EquipmentDataStateController {
    @Autowired
    BusinessEquipmentService businessEquipmentService;

    @OperLog(operModul = "设备状态管理", operType = OperType.QUERY, operDesc = "查询设备状态列表", operatorType = "后台")
    @ApiOperation(value = "查询设备状态列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<EquipmentStateDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                      PageLimit pageLimit,String lineId){
        Result result=businessEquipmentService.getEquipmentState(pageLimit,lineId);
        return result;
    }

    @OperLog(operModul = "设备状态管理", operType = OperType.UPDATE, operDesc = "修改设备指定状态", operatorType = "后台")
    @ApiOperation(value = "修改设备指定状态")
    @ApiOperationSupport(author = "jq", order = 1)
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                      @RequestBody @Valid PutEquipmentDataStateVo stateVo){
        Result result=businessEquipmentService.putEquipmentDataState(stateVo);
        return result;
    }
}
