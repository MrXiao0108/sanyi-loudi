package com.dzics.business.controller.plan;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessProductionPlanService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.plan.SelectProductionPlanVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.ProductionPlanDo;
import com.dzics.common.util.PageLimit;
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
 * 产线日生产计划设置
 * @author jq
 * Date 2021/2/19
 */
@Api(tags = {"生产计划设置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/plan")
@Controller
public class ProductionPlanController {

    @Autowired
    BusinessProductionPlanService businessProductionPlanService;

    @OperLog(operModul = "生产计划设置相关", operType = OperType.QUERY, operDesc = "查询产线日生产计划列表", operatorType = "后台")
    @ApiOperation(value = "查询产线日生产计划列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping
    public Result<ProductionPlanDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                         PageLimit pageLimit, SelectProductionPlanVo selectProductionPlanVo) {
        selectProductionPlanVo.setPlanType(FinalCode.DZ_PLAN_DAY);
        return businessProductionPlanService.list(sub,pageLimit,selectProductionPlanVo);
    }

    @OperLog(operModul = "生产计划设置相关", operType = OperType.UPDATE, operDesc = "修改产线日生产计划", operatorType = "后台")
    @ApiOperation(value = "修改产线日生产计划", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @PutMapping
    public Result<ProductionPlanDo> put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @RequestBody @Valid ProductionPlanDo productionPlanDo
    ) {
        return businessProductionPlanService.put(sub,productionPlanDo);
    }
}
