package com.dzics.business.controller.inspection;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.response.proddetection.ProDetection;
import com.dzics.common.model.request.DetectorDataQuery;
import com.dzics.business.service.BusDetectorDataService;
import com.dzics.business.service.ProductTrendChartService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 产品管理
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Api(tags = {"检测记录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/product/check")
@Controller
public class ProductCheckController {
    @Autowired
    private BusDetectorDataService busDetectorDataService;

    @Autowired
    ProductTrendChartService productTrendChartService;

    /**
     * 检测记录
     */
    @OperLog(operModul = "检测记录", operType = OperType.QUERY, operDesc = "检测记录", operatorType = "后台")
    @ApiOperation(value = "检测记录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/data")
    public Result<ProDetection> selDetectorData(@Valid DetectorDataQuery detectorDataQuery,
                                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.selDetectorData(detectorDataQuery, sub);
    }

    /**
     * 导出 检测记录
     */
//    @OperLog(operModul = "导出检测记录", operType = OperType.QUERY, operDesc = "导出检测记录", operatorType = "后台")
//    @ApiOperation(value = "导出检测记录", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperationSupport(author = "jq", order = 112)
    @GetMapping(value = "/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void getDetectorExcel(@Valid DetectorDataQuery detectorDataQuery, HttpServletResponse response,
                                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub)  {
         busDetectorDataService.getDetectorExcel1(response,detectorDataQuery, sub);
    }


}

