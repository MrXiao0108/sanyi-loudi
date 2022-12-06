package com.dzics.kanban.controller.system.kanban;

import com.dzics.kanban.common.annotation.OperLog;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.model.entity.SysInterfaceGroup;
import com.dzics.kanban.model.entity.SysInterfaceMethod;
import com.dzics.kanban.model.request.kb.*;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.service.InterfaceMethod;
import com.dzics.kanban.util.PageLimit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
@Api(tags = {"接口组合配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/route/interfaceMethod/config")
public class InterfaceMethodController {
    @Autowired
    private InterfaceMethod interfaceMethod;


    @ApiOperation(value = "接口列表")
    @GetMapping("/interface/method")
    public Result getInterfaceMethod(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                     PageLimit pageLimit) {
        Result info = interfaceMethod.getInterfaceMethod(sub, pageLimit);
        return info;
    }


    @OperLog(operModul = "接口组合配置", operType = OperType.ADD, operDesc = "新增接口", operatorType = "后台")
    @ApiOperation(value = "新增接口")
    @PostMapping("/interface/method")
    public Result addInterfaceMethod(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                     @RequestBody @Valid SysInterfaceMethod sysInterfaceMethod) {
        Result info = interfaceMethod.addInterfaceMethod(sub, sysInterfaceMethod);
        return info;
    }

    @OperLog(operModul = "接口组合配置", operType = OperType.UPDATE, operDesc = "编辑接口", operatorType = "后台")
    @ApiOperation(value = "编辑接口")
    @PutMapping("/interface/method")
    public Result editInterfaceMethod(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                      @RequestBody @Valid SysInterfaceMethod sysInterfaceMethod) {
        Result info = interfaceMethod.editInterfaceMethod(sub, sysInterfaceMethod);
        return info;
    }

    @OperLog(operModul = "接口组合配置", operType = OperType.DEL, operDesc = "删除接口", operatorType = "后台")
    @ApiOperation(value = "删除接口")
    @DeleteMapping("/interface/method")
    public Result delInterfaceMethod(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                     @RequestBody @Valid DelInterfaceMethod delInterfaceMethod) {
        Result info = interfaceMethod.delInterfaceMethod(sub, delInterfaceMethod);
        return info;
    }


    @ApiOperation(value = "组列表")
    @GetMapping("/group")
    public Result<SysInterfaceGroup> getGroup(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub, PageLimit pageLimit) {
        Result info = interfaceMethod.getGroup(sub, pageLimit);
        return info;
    }


    @OperLog(operModul = "接口组合配置", operType = OperType.ADD, operDesc = "新增组", operatorType = "后台")
    @ApiOperation(value = "新增组")
    @PostMapping("/group")
    public Result addGroup(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                           @RequestBody @Valid SysInterfaceGroup interfaceGroup) {
        Result info = interfaceMethod.addGroup(sub, interfaceGroup);
        return info;
    }

    @OperLog(operModul = "接口组合配置", operType = OperType.UPDATE, operDesc = "编辑组", operatorType = "后台")
    @ApiOperation(value = "编辑组")
    @PutMapping("/group")
    public Result editGroup(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @RequestBody @Valid SysInterfaceGroup interfaceGroup) {
        Result info = interfaceMethod.editGroup(sub, interfaceGroup);
        return info;
    }


    @OperLog(operModul = "接口组合配置", operType = OperType.DEL, operDesc = "删除组", operatorType = "后台")
    @ApiOperation(value = "删除组")
    @DeleteMapping("/group")
    public Result delGroup(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                           @RequestBody @Valid DelInterfaceGroup delInterfaceGroup) {
        Result info = interfaceMethod.delGroup(sub, delInterfaceGroup);
        return info;
    }

    @OperLog(operModul = "接口组合配置", operType = OperType.UPDATE, operDesc = "设置组接口", operatorType = "后台")
    @ApiOperation(value = "设置组接口")
    @PostMapping("/group/config")
    public Result addGroupInerfaceConfig(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                         @RequestBody @Valid AddInterfaceGroupConfiguration groupConfiguration) {
        Result info = interfaceMethod.addGroupInerfaceConfig(sub, groupConfiguration);
        return info;
    }


    @ApiOperation(value = "查看组接口")
    @GetMapping("/group/config")
    public Result<ReqGroupConfig> getGroupInerfaceConfig(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                         @Valid GetGroupConfig groupConfiguration) {
        Result info = interfaceMethod.getGroupInerfaceConfig(sub, groupConfiguration);
        return info;
    }

    @ApiOperation(value = "根据接口组点绑定方法")
    @GetMapping("/interface/method/parms")
    public Result methodGroup(@Valid KbParmsMethod kbParms,
                              @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) {
        return interfaceMethod.getMethodGroup(kbParms.getMethodGroup());
    }
}
