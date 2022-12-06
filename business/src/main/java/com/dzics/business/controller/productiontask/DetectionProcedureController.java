package com.dzics.business.controller.productiontask;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.productiontask.workingprocedure.DetectionProceduct;
import com.dzics.business.model.vo.productiontask.workingprocedure.ProcedIdproductNo;
import com.dzics.business.model.vo.productiontask.workingprocedure.SelEditDetectionProceduct;
import com.dzics.business.model.vo.productiontask.workingprocedure.UpdateDetectionProceduct;
import com.dzics.business.service.WorkingProcedureService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 工序管理
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
//@Api(tags = {"工序工件绑定"}, produces = MediaType.APPLICATION_JSON_VALUE)
//@RestController
//@RequestMapping("/api/working/detection")
//@Controller
public class DetectionProcedureController {
//    @Autowired
    private WorkingProcedureService workingProcedureService;

    @ApiOperation(value = "工序工件列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping
    public Result selProcedureProduct(
            ProcedIdproductNo productNo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.selProcedureProduct(productNo, sub);
    }


    @OperLog(operModul = "工序工件绑定", operType = OperType.ADD, operDesc = "新增工序工件", operatorType = "后台")
    @ApiOperation(value = "新增工序工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PostMapping
    public Result addProcedureProduct(
            @Valid @RequestBody DetectionProceduct detectionProceduct,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.addProcedureProduct(detectionProceduct, sub);
    }


    @ApiOperation(value = "获取单个工序工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/{orderId}/{lineId}/{workProcedProductId}")
    public Result<SelEditDetectionProceduct> selEditProcedureProduct(
            @PathVariable(value = "orderId")  @ApiParam(value = "订单id",required = true) String  orderId,
            @PathVariable(value = "lineId")  @ApiParam(value = "产线id",required = true) String  lineId,
            @PathVariable(value = "workProcedProductId")  @ApiParam(value = "工序-工件关联关系ID",required = true) String  workProcedProductId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.selEditProcedureProduct(orderId,lineId,workProcedProductId, sub);
    }

    @OperLog(operModul = "工序工件绑定", operType = OperType.UPDATE, operDesc = "编辑工序工件", operatorType = "后台")
    @ApiOperation(value = "编辑工序工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PutMapping
    public Result updateProcedureProduct(
            @Valid @RequestBody UpdateDetectionProceduct detectionProceduct,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.updateProcedureProduct(detectionProceduct, sub);
    }


    @ApiOperation(value = "获取产品检测项", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/{orderId}/{lineId}/productNo/{productNo}")
    public Result selProductTemplate(
            @PathVariable(value = "orderId", required = true) @ApiParam(value = "订单id",required = true) String orderId,
            @PathVariable(value = "lineId", required = true) @ApiParam(value = "产线id",required = true) String lineId,
            @PathVariable(value = "productNo", required = true) @ApiParam(value = "产品ID",required = true) String productNo,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.selProductTemplate(orderId, lineId, productNo, sub);
    }

    @OperLog(operModul = "工序工件绑定", operType = OperType.DEL, operDesc = "删除工序工件", operatorType = "后台")
    @ApiOperation(value = "删除工序工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @DeleteMapping("/{workProcedProductId}")
    public Result delWorkProcedProductId(
            @PathVariable(value = "workProcedProductId")  @ApiParam(value = "工序-工件关联关系ID",required = true) String  workProcedProductId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.delWorkProcedProductId(workProcedProductId, sub);
    }
}
