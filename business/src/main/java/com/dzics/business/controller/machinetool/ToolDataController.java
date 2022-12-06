package com.dzics.business.controller.machinetool;

import com.dzics.common.model.constant.FinalCode;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.common.model.request.SelectEquipmentDataVo;
import com.dzics.common.model.response.EquipmentDataDo;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"机床历史数据"}, produces = "机床历史数据管理关接口")
@RequestMapping("/toolData")
@RestController
public class ToolDataController {
    @Autowired
    BusinessEquipmentService dzEquipmentService;

//    @OperLog(operModul = "分页查询机床数据列表", operType = OperType.QUERY, operDesc = "分页查询机床数据列表", operatorType = "后台")
    @ApiOperation(value = "分页查询机床数据列表 ")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public Result<EquipmentDataDo> listEquipmentData(SelectEquipmentDataVo selectEquipmentDataVo,
                                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ){
        Result listResult = dzEquipmentService.listEquipmentData(sub, FinalCode.TOOL_EQUIPMENT_CODE, selectEquipmentDataVo);
        return listResult;
    }
}
