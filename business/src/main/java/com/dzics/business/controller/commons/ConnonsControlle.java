package com.dzics.business.controller.commons;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.DeviceParms;
import com.dzics.business.service.*;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.*;
import com.dzics.common.model.request.depart.DepartParms;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.commons.Lines;
import com.dzics.common.model.response.commons.Orders;
import com.dzics.common.model.response.commons.Products;
import com.dzics.common.model.response.commons.SelOrders;
import com.dzics.common.model.response.device.DeviceMessage;
import com.dzics.common.model.response.devicecheck.DzCheckUpItemDo;
import com.dzics.common.service.DzMaterialService;
import com.dzics.common.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

/**
 * 公共接口
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Api(tags = {"公共接口"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/commons")
@Controller
public class ConnonsControlle {
    @Autowired
    private BusinessDictItemService dictItemService;

    @Autowired
    private BusinessOrderService businessOrderService;

    @Autowired
    private WorkingProcedureService workingProcedureService;

    @Autowired
    private BusinessDzProductionLineService lineService;

    @Autowired
    private DataDeviceService dataDeviceService;
    @Autowired
    private BusinessEquipmentService equipmentService;

    @Autowired
    DzMaterialService dzMaterialService;

    @Autowired
    private CheckUpItemService checkUpItemService;

    @ApiOperation(value = "根据站点获取订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/orders/depart")
    public Result<Orders> selOrdersDepart(DepartParms departParms,
                                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessOrderService.selOrdersDepart(departParms, sub);
    }

    @ApiOperation(value = "所有订单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping("/orders")
    public Result<Orders> selOrders(SelOrders selOrders, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessOrderService.setlOrders(selOrders, sub);
    }

    @ApiOperation(value = "所有产线", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 113)
    @GetMapping("/lines")
    public Result<Lines> selLines(SelOrders selOrders, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessOrderService.selLines(selOrders, sub);
    }

    @ApiOperation(value = "所有工件", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 114)
    @GetMapping("/products")
    public Result<Products> selProduct(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                       @RequestParam(value = "lineType", required = false) String lineType) {
        return businessOrderService.selProduct(sub, lineType);
    }


    @ApiOperation(value = "所有工序", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 115)
    @GetMapping("/working/procedures")
    public Result getWorkingProcedures(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return workingProcedureService.getWorkingProcedures(sub);
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "根据订单id查询产线列表", operatorType = "后台")
    @ApiOperation(value = "根据订单id查询产线列表")
    @ApiOperationSupport(author = "jq", order = 116)
    @GetMapping("/getByOrderId/{id}")
    public Result<DzProductionLine> getByOrderId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                 @PathVariable("id") String id
    ) {
        return lineService.getByOrderId(sub, Long.valueOf(id));
    }

    @OperLog(operModul = "产线相关", operType = OperType.QUERY, operDesc = "根据订单id查询产线列表", operatorType = "后台")
    @ApiOperation(value = "获取对应订单下所有产线V2")
    @ApiOperationSupport(author = "jq", order = 117)
    @GetMapping("/getByOrderId/v2/{id}")
    public Result<Lines> getByOrderIdV2(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @PathVariable("id") String id
    ) {
        return lineService.getByOrderIdV2(sub, Long.valueOf(id));
    }

    @OperLog(operModul = "字典item值相关", operType = OperType.QUERY, operDesc = "查询字典item值列表", operatorType = "后台")
    @ApiOperation(value = "查询字典item值列表")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping("/dict/item")
    public Result<SysDictItem> listDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                            @RequestParam("dictId") Integer dictId, PageLimit pageLimit) {
        return dictItemService.listDictItem(pageLimit, dictId);
    }


    @OperLog(operModul = "三一设备管理", operType = OperType.QUERY, operDesc = "查询未绑定第三方的大正设备列表", operatorType = "后台")
    @ApiOperation(value = "查询未绑定第三方的大正设备列表")
    @GetMapping("/collection/dzDataDevice")
    public Result getByKey(@Valid DeviceParms deviceParms,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dataDeviceService.getDzEquipment(deviceParms);
    }


    @OperLog(operModul = "三一设备管理", operType = OperType.QUERY, operDesc = "查询未绑定第三方的大正设备列表", operatorType = "后台")
    @ApiOperation(value = "查询设备订单产线下所有设备列表")
    @GetMapping("/order/linedevice")
    public Result getByKeyDeviceAll(@Valid DeviceParms deviceParms,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dataDeviceService.getByKeyDeviceAll(deviceParms);
    }

    @OperLog(operModul = "组件物料", operType = OperType.QUERY, operDesc = "根据产品id查询组件物料列表", operatorType = "后台")
    @ApiOperation(value = "查询产品绑定的组件物料列表")
    @GetMapping("/product/material/{productId}")
    public Result<DzMaterial> getMaterialByProductId(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                     @PathVariable("productId")  String productId) {
        return dzMaterialService.getMaterialByProductId(productId);
    }

    @OperLog(operModul = "字典item值相关", operType = OperType.QUERY, operDesc = "查询字典item值列表", operatorType = "后台")
    @ApiOperation(value = "根据dictCode查询字典数据")
    @ApiOperationSupport(author = "jq", order = 4)
    @GetMapping("/getDictItem/{dictCode}")
    public Result<SysDictItem> getDictItem(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @PathVariable("dictCode") String dictCode) {
        return dictItemService.getDictItem(dictCode);
    }


    @ApiOperation(value = "产线下所有设备", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 114)
    @GetMapping("/line/devcie")
    public Result<DeviceMessage> selLineDevice(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                               @RequestParam(value = "lineId") String lineId) {
        return equipmentService.getDevcieLineId(sub,lineId);
    }

    @OperLog(operModul = "巡检项设置管理", operType = OperType.QUERY, operDesc = "查询巡检项列表", operatorType = "后台")
    @ApiOperation(value = "查询巡检项列表")
    @GetMapping("/check/up/getList")
    public Result<DzCheckUpItemDo> getCheckList(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        PageLimit pageLimit,Integer equipmentType){
        Result result =checkUpItemService.list(pageLimit,equipmentType,null);
        return result;
    }
}
