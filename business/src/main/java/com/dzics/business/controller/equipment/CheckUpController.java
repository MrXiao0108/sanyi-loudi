package com.dzics.business.controller.equipment;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.CheckUpItemService;
import com.dzics.business.util.PageUtil;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.DzCheckUpItem;
import com.dzics.common.model.request.devicecheck.CheckUpVo;
import com.dzics.common.model.request.devicecheck.DeviceCheckVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.devicecheck.DzCheckUpItemDo;
import com.dzics.common.service.DzCheckUpItemService;
import com.dzics.common.util.PageLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"巡检项设置管理"}, produces = "巡检项设置管理相关接口")
@RestController
@RequestMapping("/api/check/up")
public class CheckUpController {

    @Autowired
    private CheckUpItemService checkUpItemService;
    @OperLog(operModul = "巡检项设置管理", operType = OperType.ADD, operDesc = "添加巡检项", operatorType = "后台")
    @ApiOperation(value = "添加巡检项")
    @PostMapping
    public Result add(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid CheckUpVo checkUpVo){
        Result result =checkUpItemService.add(sub,checkUpVo);
        return result;
    }
    @OperLog(operModul = "巡检项设置管理", operType = OperType.QUERY, operDesc = "查询巡检项列表", operatorType = "后台")
    @ApiOperation(value = "查询巡检项列表")
    @GetMapping
    public Result<DzCheckUpItemDo> list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        PageLimit pageLimit, Integer deviceType, String checkName){
        Result result =checkUpItemService.list(pageLimit,deviceType,checkName);
        return result;
    }

    @OperLog(operModul = "巡检项设置管理", operType = OperType.DEL, operDesc = "删除巡检项", operatorType = "后台")
    @ApiOperation(value = "删除巡检项")
    @DeleteMapping("/{checkItemId}")
    public Result del(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @PathVariable("checkItemId")String checkItemId){
        Result result =checkUpItemService.del(checkItemId);
        return result;
    }

    @OperLog(operModul = "巡检项设置管理", operType = OperType.UPDATE, operDesc = "编辑巡检项", operatorType = "后台")
    @ApiOperation(value = "编辑巡检项")
    @PutMapping
    public Result put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                      @RequestBody @Valid CheckUpVo checkUpVo){
        Result result =checkUpItemService.put(sub,checkUpVo);
        return result;
    }
}
