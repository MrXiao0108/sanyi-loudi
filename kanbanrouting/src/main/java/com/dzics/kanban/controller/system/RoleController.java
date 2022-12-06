package com.dzics.kanban.controller.system;


import com.dzics.kanban.common.annotation.OperLog;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.model.entity.SysRole;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.model.vo.rolemenu.AddRole;
import com.dzics.kanban.model.vo.rolemenu.DisableEnabledRole;
import com.dzics.kanban.model.vo.rolemenu.SelRole;
import com.dzics.kanban.model.vo.rolemenu.UpdateRole;
import com.dzics.kanban.service.BusinessUserService;
import com.dzics.kanban.service.SysUserServiceDao;
import com.dzics.kanban.util.PageLimit;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Api(tags = {"角色管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user")
@Controller
public class RoleController {
    @Autowired
    private BusinessUserService businessUserService;
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
//    @OperLog(operModul = "角色管理", operType = OperType.QUERY, operDesc = "角色列表", operatorType = "后台")
    @ApiOperation(value = "角色列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 110)
    @GetMapping(value = "/role")
    public Result<SysRole> getRoles(PageLimit pageLimit, SelRole selRole,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.getRoles(sub,pageLimit,selRole);
    }

//    @OperLog(operModul = "角色管理", operType = OperType.QUERY, operDesc = "角色详情", operatorType = "后台")
    @ApiOperation(value = "角色详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 110)
    @GetMapping(value = "/role/{roleId}")
    public Result<SysRole> getRolesDetails(@PathVariable("roleId") Long roleId,
                                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.getRolesDetails(roleId, sub);
    }

    @OperLog(operModul = "角色管理", operType = OperType.ADD, operDesc = "新增角色", operatorType = "后台")
    @ApiOperation(value = "新增角色", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PostMapping(value = "/role")
    public Result addRole(
            @Valid @RequestBody AddRole addRole,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.addRole(addRole, sub);
    }

    @OperLog(operModul = "角色管理", operType = OperType.UPDATE, operDesc = "编辑角色", operatorType = "后台")
    @ApiOperation(value = "编辑角色", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 113)
    @PutMapping(value = "/update/role")
    public Result updateRole(
            @Valid @RequestBody UpdateRole updateRole,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.updateRole(updateRole, sub);
    }

    @OperLog(operModul = "角色管理", operType = OperType.UPDATE, operDesc = "禁用启用角色", operatorType = "后台")
    @ApiOperation(value = "禁用启用角色", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 114)
    @PutMapping(value = "/disable/enabled/role")
    public Result disableEnabledRole(
            @Valid @RequestBody DisableEnabledRole enabledRole,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.disableEnabledRole(enabledRole, sub);
    }

    @OperLog(operModul = "角色管理", operType = OperType.DEL, operDesc = "删除角色", operatorType = "后台")
    @ApiOperation(value = "删除角色", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 115)
    @DeleteMapping(value = "/role/{roleId}")
    public Result delRole(
            @PathVariable("roleId") Long roleId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.delRole(roleId, sub);
    }


//    @OperLog(operModul = "角色管理", operType = OperType.QUERY, operDesc = "树形菜单列表", operatorType = "后台")
    @ApiOperation(value = "树形菜单列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 115)
    @GetMapping(value = "/tree/select")
    public Result treeSelect(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        return businessUserService.treeSelect(sub,byUserName);
    }

//    @OperLog(operModul = "角色管理", operType = OperType.QUERY, operDesc = "树形菜单列表已选择的", operatorType = "后台")
    @ApiOperation(value = "树形菜单列表已选择的", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 115)
    @GetMapping(value = "/tree/select/check/{roleId}")
    public Result treeSelectCheck(
            @PathVariable("roleId") Long roleId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.treeSelectCheck(roleId, sub);
    }
}
