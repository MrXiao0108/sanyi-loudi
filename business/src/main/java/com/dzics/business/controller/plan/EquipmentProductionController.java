package com.dzics.business.controller.plan;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessEquipmentProNumService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.plan.SelectEquipmentProductionDetailsVo;
import com.dzics.common.model.request.plan.SelectEquipmentProductionVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.SelectEquipmentProductionDo;
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
 * 设备生产数量明细
 * @author jq
 * Date 2021/2/20
 */
@Api(tags = {"设备生产数量明细"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/equipment")
@Controller
public class EquipmentProductionController {
    @Autowired
    BusinessEquipmentProNumService businessEquipmentProNumService;

    @OperLog(operModul = "产品生产明细相关", operType = OperType.QUERY, operDesc = "设备生产数量明细列表", operatorType = "后台")
    @ApiOperation(value = "设备生产数量明细列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping
    public Result<SelectEquipmentProductionDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                    PageLimit pageLimit, SelectEquipmentProductionVo selectProductionDetailsVo
    ) {
        Result result =businessEquipmentProNumService.listProductionEquipment(sub,pageLimit,selectProductionDetailsVo);
        return result;
    }

    @OperLog(operModul = "产品生产明细相关", operType = OperType.QUERY, operDesc = "设备生产数量详情列表", operatorType = "后台")
    @ApiOperation(value = "设备生产数量详情列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/listDetails")
    public Result listDetails(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              SelectEquipmentProductionDetailsVo selectEquipmentProductionDetailsVo
    ) {
        Result result =businessEquipmentProNumService.listProductionEquipmentDetails(sub,selectEquipmentProductionDetailsVo);
        return result;
    }
}

