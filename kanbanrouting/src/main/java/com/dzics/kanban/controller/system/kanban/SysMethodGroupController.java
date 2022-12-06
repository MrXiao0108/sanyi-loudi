package com.dzics.kanban.controller.system.kanban;

import com.dzics.kanban.common.annotation.OperLog;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.model.request.DelGpOrMdVo;
import com.dzics.kanban.model.request.UpGpOrMdVo;
import com.dzics.kanban.model.response.GroupsMethods;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.service.SysInterfaceMethodService;
import com.dzics.kanban.service.SysMethodGroupService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 方法组表 前端控制器
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-02
 */
@SuppressWarnings("ALL")
@Api(tags = {"组明细管理"},produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/route/sysMethodGroup")
public class SysMethodGroupController {

    private SysMethodGroupService groupService;

    private SysInterfaceMethodService sysInterfaceMethodService;

    public SysMethodGroupController(SysMethodGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping("/getGroups")
    @ApiOperation(value = "下拉查询所有组", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Xiaonb", order = 1)
    public Result<List<GroupsMethods>> getDropdown(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return Result.ok(groupService.getGroup());
    }

    @GetMapping("/getGroupsWithConfig")
    @ApiOperation(value = "查询所有组及接口列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "Xiaonb", order = 2)
    public Result<List<GroupsMethods>>getGroupsWithConfig(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        List<GroupsMethods> groupsWithConfig = groupService.getGroupsWithConfig(sub);
        return Result.ok(groupsWithConfig);
    }

    @PostMapping("/addGroupOrMethod")
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @OperLog(operModul = "接口组管理", operType = OperType.ADD, operDesc = "添加")
    @ApiOperationSupport(author = "Xiaonb", order = 3)
    public Result addGroupOrMethod(@Valid @RequestBody UpGpOrMdVo addGpOrMdVo,
                                   @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return groupService.addGroupOrMethod(sub, addGpOrMdVo);
    }

    @DeleteMapping("/delGroupOrMethod")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @OperLog(operModul = "接口组管理", operType = OperType.DEL, operDesc = "删除")
    @ApiOperationSupport(author = "Xiaonb", order = 4)
    public Result delGroupOrMethod(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                   @RequestBody @Valid DelGpOrMdVo delGpOrMdVo) {
        return groupService.delGroupOrMethod(sub, delGpOrMdVo);
    }

    @PutMapping("/upGpOrMd")
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE)
    @OperLog(operModul = "接口组管理", operType = OperType.UPDATE, operDesc = "修改")
    @ApiOperationSupport(author = "Xiaonb", order = 5)
    public Result upGpOrMd(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                           @RequestBody @Valid UpGpOrMdVo upGpOrMdVo){
        return groupService.upInterfaceByGroup(sub, upGpOrMdVo);
    }


}

