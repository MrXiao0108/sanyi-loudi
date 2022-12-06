package com.dzics.business.controller.productiontask;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.productiontask.workingprocedure.UpdateDetectionProceduct;
import com.dzics.business.service.WorkingProcedureService;
import com.dzics.business.service.WorkingStationProductService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.locationartifacts.AddLocationArtifactsVo;
import com.dzics.common.model.request.locationartifacts.LocationArtifactsVo;
import com.dzics.common.model.request.locationartifacts.PutLocationArtifactsVo;
import com.dzics.common.model.request.product.OrderLinePrms;
import com.dzics.common.model.request.product.OrderLinePrmsWork;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.locationartifacts.GetLocationArtifactsByIdDo;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"工位工件绑定"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/location/artifacts")
@Controller
public class LocationArtifactsController {
    @Autowired
    private WorkingProcedureService workingProcedureService;
    @Autowired
    private WorkingStationProductService workingStationProductService;

    @ApiOperation(value = "工位工件列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 200)
    @GetMapping
    public Result list(
            LocationArtifactsVo locationArtifactsVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingStationProductService.locationArtifactsList(locationArtifactsVo, sub);
    }

    @ApiOperation(value = "工位工件添加", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 201)
    @PostMapping
    public Result add(
            @Valid @RequestBody AddLocationArtifactsVo addLocationArtifactsVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingStationProductService.add(addLocationArtifactsVo, sub);
    }

    @ApiOperation(value = "获取单个工位工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @GetMapping("/get")
    public Result<GetLocationArtifactsByIdDo> getById(
            OrderLinePrmsWork orderId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingStationProductService.selEditProcedureProduct(orderId.getOrderId(), orderId.getLineId(), orderId.getWorkStationProductId(), sub);
    }

    @OperLog(operModul = "工位工件绑定", operType = OperType.UPDATE, operDesc = "编辑工位工件", operatorType = "后台")
    @ApiOperation(value = "编辑工位工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @PutMapping
    public Result updateProcedureProduct(
            @Valid @RequestBody PutLocationArtifactsVo putLocationArtifactsVo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingStationProductService.updateProcedureProduct(putLocationArtifactsVo, sub);
    }

    @ApiOperation(value = "获取产品检测项", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @GetMapping("/productNo")
    public Result selProductTemplate(
            OrderLinePrms orderId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.selProductTemplate(orderId.getOrderId(), orderId.getLineId(), orderId.getProductNo(), sub);
    }

    @OperLog(operModul = "工位工件绑定", operType = OperType.DEL, operDesc = "删除工位工件", operatorType = "后台")
    @ApiOperation(value = "删除工位工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 112)
    @DeleteMapping("/{workStationProductId}")
    public Result delWorkStationProductId(
            @PathVariable(value = "workStationProductId") @ApiParam(value = "工位-工件关联关系ID", required = true) String workStationProductId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingStationProductService.delWorkStationProductId(workStationProductId, sub);
    }
}
