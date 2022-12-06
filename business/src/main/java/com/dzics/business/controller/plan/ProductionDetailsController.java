package com.dzics.business.controller.plan;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusinessEquipmentProNumService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.plan.SelectProductionDetailsVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.SelectProductionDetailsDo;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 产品生产明细
 *
 * @author jq
 * Date 2021/2/20
 */
@Api(tags = {"产品生产明细"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/production/details")
@Controller
public class ProductionDetailsController {
    @Autowired
    BusinessEquipmentProNumService businessEquipmentProNumService;

    @OperLog(operModul = "产品生产明细相关", operType = OperType.QUERY, operDesc = "产品生产明细列表", operatorType = "后台")
    @ApiOperation(value = "产品生产明细列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping
    public Result<SelectProductionDetailsDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                  PageLimit pageLimit, SelectProductionDetailsVo selectProductionDetailsVo
    ) {
        Result result = businessEquipmentProNumService.list(sub, pageLimit, selectProductionDetailsVo);
        return result;
    }
}
