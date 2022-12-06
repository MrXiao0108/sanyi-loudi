package com.dzics.business.controller.equipment;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.CheckHistoryService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzCheckHistoryItem;
import com.dzics.common.model.request.devicecheck.DeviceCheckVo;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.devicecheck.DzCheckUpItemDo;
import com.dzics.common.model.response.devicecheck.GetDeviceCheckDo;
import com.dzics.common.util.PageLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Api(tags = {"设备巡检项记录管理"}, produces = "设备巡检项记录管理相关接口")
@RestController
@RequestMapping("/api/device/check")
public class DeviceCheckController {

    @Autowired
    CheckHistoryService checkHistoryService;

    @OperLog(operModul = "巡检项记录管理", operType = OperType.ADD, operDesc = "添加设备巡检项记录", operatorType = "后台")
    @ApiOperation(value = "添加设备巡检项记录")
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid DeviceCheckVo deviceCheckVo){
        Result result=checkHistoryService.add(sub,deviceCheckVo);
        return result;
    }
    @OperLog(operModul = "巡检项记录管理", operType = OperType.QUERY, operDesc = "查询巡检项记录", operatorType = "后台")
    @ApiOperation(value = "查询巡检项记录")
    @GetMapping
    public Result<GetDeviceCheckDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                         GetDeviceCheckVo getDeviceCheckVo){
        Result result =checkHistoryService.list(getDeviceCheckVo);
        return result;
    }

    @OperLog(operModul = "巡检项记录管理", operType = OperType.QUERY, operDesc = "查询巡检项记录详情", operatorType = "后台")
    @ApiOperation(value = "查询巡检项记录详情")
    @GetMapping("/{checkHistoryId}")
    public Result<DzCheckHistoryItem> getById(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @PathVariable("checkHistoryId") String checkHistoryId){
        Result result =checkHistoryService.getById(checkHistoryId);
        return result;
    }
    @OperLog(operModul = "巡检项记录管理", operType = OperType.UPDATE, operDesc = "修改巡检项记录", operatorType = "后台")
    @ApiOperation(value = "修改巡检项记录")
    @PutMapping
    public Result<DzCheckHistoryItem> put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                              @RequestBody List<DzCheckHistoryItem> list){
        Result result =checkHistoryService.put(sub,list);
        return result;
    }
    @OperLog(operModul = "巡检项记录管理", operType = OperType.DEL, operDesc = "删除巡检项记录", operatorType = "后台")
    @ApiOperation(value = "删除巡检项记录")
    @DeleteMapping("/{checkHistoryId}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                          @PathVariable("checkHistoryId") String checkHistoryId){
        Result result =checkHistoryService.del(sub,checkHistoryId);
        return result;
    }
}
