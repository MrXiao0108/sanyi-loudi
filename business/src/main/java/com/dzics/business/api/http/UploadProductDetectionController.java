package com.dzics.business.api.http;

import com.alibaba.fastjson.JSONObject;
import com.dzics.business.service.BuProductionQuantityService;
import com.dzics.business.service.BusinessOrderService;
import com.dzics.business.service.BusinessProductionPlanService;
import com.dzics.business.service.WorkpieceDataService;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.request.UploadProductDetectionVo;
import com.dzics.common.model.request.charts.IntelligentDetectionVo;
import com.dzics.common.model.request.kb.GetOrderNoLineNo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.commons.Lines;
import com.dzics.common.model.response.commons.Products;
import com.dzics.common.model.response.commons.SelOrders;
import com.dzics.common.service.DzWorkpieceDataService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;

@Api(tags = {"数据上传"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping
@RestController
public class UploadProductDetectionController {

    @Autowired
    WorkpieceDataService workpieceDataService;

    @Autowired
    BuProductionQuantityService productionQuantityService;
    @Autowired
    DzWorkpieceDataService dzWorkpieceDataService;
    @Autowired
    private BusinessOrderService businessOrderService;
    @Autowired
    private BusinessProductionPlanService businessProductionPlanService;

    /**
     * 产品检测上传接口
     *
     * @param uploadProductDetectionVo
     * @return
     */
    @PostMapping("/upload/product/detection")
    @ApiOperation(value = "产品检测上传接口")
    public Result uploadProductDetection(@RequestBody @Valid UploadProductDetectionVo uploadProductDetectionVo) {

        Result result = workpieceDataService.uploadProductDetectionVo(uploadProductDetectionVo);
        return result;
    }
    /**
     * 后台智能检测中心  通用接口  所有产线
     */
    @ApiOperation(value = "所有产线", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 113)
    @GetMapping("/api/general/intelligent/detection/getAllLines")
    public Result<Lines> selLines(SelOrders selOrders) {
        return businessOrderService.selLines(selOrders, "admin");
    }
    /**
     * 后台智能检测中心  通用接口  所有工件
     */
    @ApiOperation(value = "所有工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 114)
    @GetMapping("/api/general/intelligent/detection/getAllProducts")
    public Result<Products> selProduct(@RequestParam(value = "lineType", required = false) String lineType) {
        return businessOrderService.selProduct("admin", lineType);
    }
    /**
     * 后台智能检测中心  通用接口  智能检测系统
     */
//    @ApiOperation(value = "智能检测系统", consumes = MediaType.APPLICATION_JSON_VALUE)
//    @ApiOperationSupport(author = "jq", order = 4)
//    @PostMapping(value = "/api/general/intelligent/detection/list")
//    public Result intelligentDetection(@RequestBody @Valid IntelligentDetectionVo intelligentDetectionVo) throws ParseException {
//        return businessProductionPlanService.intelligentDetection("admin", intelligentDetectionVo);
//    }

    /**
     * 测试看板功能接口
     */
    @GetMapping("/upload/product/detection")
    public String getOutputByLineId(@RequestParam("day")Integer day) {
        GetOrderNoLineNo getOrderNoLineNo = new GetOrderNoLineNo();
        getOrderNoLineNo.setOrderNo("DZ-1875");
        getOrderNoLineNo.setLineNo("1");
        getOrderNoLineNo.setDayNumber(day);
        Result result = productionQuantityService.getProductionLineNumberByDay(getOrderNoLineNo);
        return JSONObject.toJSONString(result);

//        DzWorkpieceData byId = dzWorkpieceDataService.getById("1427591928205426690");
//        return JSONObject.toJSONString(byId);
    }
}

