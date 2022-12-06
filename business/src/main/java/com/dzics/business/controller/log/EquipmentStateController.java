package com.dzics.business.controller.log;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessEquipmentStateLogService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.dzics.common.model.request.SelectEquipmentStateVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(tags = {"设备运行状态管理"}, produces = "设备运行状态管理相关接口")
@RestController
@RequestMapping("/equipmentState/log")
public class EquipmentStateController {

    @Autowired
    BusinessEquipmentStateLogService businessEquipmentStateLogService;


    @OperLog(operModul = "设备运行状态管理", operType = OperType.QUERY, operDesc = "设备运行状态日志查询", operatorType = "后台")
    @ApiOperation(value = "设备运行状态日志查询")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping
    public Result<DzEquipmentStateLog> list(PageLimit pageLimit,@Valid SelectEquipmentStateVo selectEquipmentStateVo,
                                            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ){
        return  businessEquipmentStateLogService.list(sub,pageLimit,selectEquipmentStateVo);
    }

}
