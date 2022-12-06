package com.dzics.business.controller.dzmom;


import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.DataDeviceService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzDataDevice;
import com.dzics.common.model.request.datadevice.AddDataDeviceVo;
import com.dzics.common.model.request.datadevice.GetDataDeviceVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.sany.SanyDeviceData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 三一设备管理
 *
 * @author jq
 * 2021-7-21
 */
@Api(tags = {"三一设备管理"}, produces = "三一设备管理相关接口")
@RestController
@RequestMapping("/collection/dzDataDevice")
public class DataDeviceController {

    @Autowired
    private DataDeviceService dataDeviceService;

    @OperLog(operModul = "三一设备管理", operType = OperType.ADD, operDesc = "添加设备", operatorType = "后台")
    @ApiOperation(value = "添加设备")
    @PostMapping
    public Result<DzDataDevice> add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    @RequestBody @Valid AddDataDeviceVo dataDeviceVo) {
        return dataDeviceService.add(dataDeviceVo);
    }

    @OperLog(operModul = "三一设备管理", operType = OperType.QUERY, operDesc = "查询设备列表", operatorType = "后台")
    @ApiOperation(value = "查询设备列表")
    @GetMapping
    public Result<SanyDeviceData> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                       GetDataDeviceVo dataDeviceVo) {
        return dataDeviceService.list(dataDeviceVo);
    }

    @OperLog(operModul = "三一设备管理", operType = OperType.UPDATE, operDesc = "编辑设备", operatorType = "后台")
    @ApiOperation(value = "编辑设备")
    @PutMapping
    public Result<DzDataDevice> update(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                       @RequestBody @Valid AddDataDeviceVo dataDeviceVo) {
        return dataDeviceService.update(dataDeviceVo);
    }

    @OperLog(operModul = "三一设备管理", operType = OperType.DEL, operDesc = "删除设备", operatorType = "后台")
    @ApiOperation(value = "删除设备")
    @DeleteMapping("/{deviceKey}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @PathVariable("deviceKey") Long deviceKey) {
        Integer deviceType = dataDeviceService.getById(deviceKey);
        AddDataDeviceVo addDataDeviceVo = new AddDataDeviceVo();
        addDataDeviceVo.setDeviceKey(deviceKey);
        addDataDeviceVo.setDeviceType(deviceType);
        return dataDeviceService.del(addDataDeviceVo);
    }

    @OperLog(operModul = "三一设备管理", operType = OperType.QUERY, operDesc = "根据设备key查询详情", operatorType = "后台")
    @ApiOperation(value = "根据设备key查询详情")
    @GetMapping("/{deviceKey}")
    public Result<DzDataDevice> getByKey(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                         @PathVariable("deviceKey") Long deviceKey) {
        return dataDeviceService.getByKey(deviceKey);
    }


}

