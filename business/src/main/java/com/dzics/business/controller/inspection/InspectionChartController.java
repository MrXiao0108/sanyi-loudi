package com.dzics.business.controller.inspection;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusDetectorDataService;
import com.dzics.business.service.BusinessProductService;
import com.dzics.business.service.ProductTrendChartService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzProduct;
import com.dzics.common.model.entity.DzProductDetectionTemplate;
import com.dzics.common.model.request.SelectTrendChartVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.SelectTrendChartDo;
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
 * 检测值走势图
 *
 * @author ZhangChengJun
 * Date 2021/2/21.
 * @since
 */
@Api(tags = {"检测值走势图"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/product/check")
@Controller
public class InspectionChartController {
    @Autowired
    private BusDetectorDataService busDetectorDataService;
    @Autowired
    BusinessProductService businessProductService;
    @Autowired
    private ProductTrendChartService productTrendChartService;

    @OperLog(operModul = "检测值走势图", operType = OperType.QUERY, operDesc = "检测走势图列表", operatorType = "后台")
    @ApiOperation(value = "检测走势图列表")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping("/trend")
    public Result<SelectTrendChartDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                           @Valid SelectTrendChartVo selectTrendChartVo) {
        Result result = productTrendChartService.list(sub, selectTrendChartVo);
        return result;
    }

    @OperLog(operModul = "检测管理", operType = OperType.QUERY, operDesc = "根据产品id(序号)查询检测项", operatorType = "后台")
    @ApiOperation(value = "根据产品id查询检测项")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/trend/getByProductId/{productNo}")
    public Result<DzProductDetectionTemplate> getByProductId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                             @PathVariable("productNo") @ApiParam("产品id(序号)") String productNo) {
        Result result = productTrendChartService.getByProductId(productNo);
        return result;
    }

    @OperLog(operModul = "检测管理", operType = OperType.QUERY, operDesc = "根据站点id查询产品列表", operatorType = "后台")
    @ApiOperation(value = "根据站点id查询产品列表")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping("/trend/getDepartId/{departId}")
    public Result<DzProduct> getByProductId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                            @PathVariable("departId") @ApiParam("站点id") Long departId) {
        Result result = businessProductService.getByProductId(departId);
        return result;
    }


}
