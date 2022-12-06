package com.dzics.business.controller.dzmom;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.BusMomOrderService;
import com.dzics.common.enums.OperType;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.mom.AddMomOrder;
import com.dzics.common.model.request.mom.GetMomOrderVo;
import com.dzics.common.model.request.mom.MaterialAddParms;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetMomOrderDo;
import com.dzics.common.service.MomOrderService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Api(tags = {"MOM订单管理"}, produces = "MOM订单管理相关")
@RequestMapping("/api/mom/order")
@RestController
public class MomOrderController {

    @Autowired
    private BusMomOrderService busMomOrderService;
    @Autowired
    private MomOrderService momOrderService;

    @OperLog(operModul = "MOM订单管理", operType = OperType.ADD, operDesc = "新增订单", operatorType = "后台")
    @ApiOperation(value = "新增订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping
    public Result addOrder(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                           @RequestBody @Valid AddMomOrder momOrder) {
        return busMomOrderService.addOrder(momOrder, sub);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.QUERY, operDesc = "查询订单", operatorType = "后台")
    @ApiOperation(value = "查询订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @GetMapping
    public Result<GetMomOrderDo> getMomOderList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub, GetMomOrderVo getMomOrderVO) {
        return momOrderService.getMomOderList(getMomOrderVO, sub);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.QUERY, operDesc = "查询组件物料详情", operatorType = "后台")
    @ApiOperation(value = "查询组件物料详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @GetMapping("/{workingProcedureId}")
    public Result<MaterialAddParms> getMaterialDetails(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                       @PathVariable("workingProcedureId") @ApiParam(value = "工序id", required = true) String mom_order_id) {
        return momOrderService.getMaterialDetails(mom_order_id, sub);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "订单开始按钮", operatorType = "后台")
    @ApiOperation(value = "订单开始按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid PutMomOrder putMomOrder) {
        return busMomOrderService.put(sub, putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "强制关闭", operatorType = "后台")
    @ApiOperation(value = "强制关闭", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PutMapping("/force/close")
    public Result forceClose(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                             @RequestBody @Valid PutMomOrder putMomOrder) {
        return busMomOrderService.forceClose(sub, putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "订单暂停", operatorType = "后台")
    @ApiOperation(value = "订单暂停", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PutMapping("/order/stop")
    public Result orderStop(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @RequestBody @Valid PutMomOrder putMomOrder) {
        return busMomOrderService.orderStop(sub, putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "订单恢复", operatorType = "后台")
    @ApiOperation(value = "订单恢复", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PutMapping("/order/recover")
    public Result orderRecover(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                               @RequestBody @Valid PutMomOrder putMomOrder) {
        return busMomOrderService.orderRecover(sub, putMomOrder);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "订单作废", operatorType = "后台")
    @ApiOperation(value = "订单作废", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @DeleteMapping("/order/{proTaskOrderId}")
    public Result orderDelete(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @PathVariable(value = "proTaskOrderId", required = true) String proTaskOrderId) {
        return busMomOrderService.orderDelete(sub, proTaskOrderId);
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "强制关闭转发", operatorType = "后台")
    @ApiOperation(value = "强制关闭转发", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PostMapping("/transPond/force/close")
    public Result transPond(@RequestBody @Valid PutMomOrder putMomOrder) {
        boolean b = putMomOrder != null ? "dzicsdzics".equals(putMomOrder.getTransPondKey()) : false;
        if (b) {
            return busMomOrderService.forceClose(null, putMomOrder);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode(), "权限校验失败");
    }

    @OperLog(operModul = "MOM订单管理", operType = OperType.UPDATE, operDesc = "取消报工", operatorType = "后台")
    @ApiOperation(value = "取消报工", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 9)
    @DeleteMapping("/order/workReporting/close/{proTaskOrderId}")
    public Result cancelWorkReporting(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                      @PathVariable(value = "proTaskOrderId") String proTaskOrderId) {
        return busMomOrderService.orderCancelWorkReporting(sub, proTaskOrderId);
    }

}
