package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.RegisterVo;
import com.dzics.business.model.vo.user.DisableEnabledUser;
import com.dzics.business.model.vo.user.ResetUser;
import com.dzics.business.model.vo.user.SelUser;
import com.dzics.business.model.vo.user.UpdateUser;
import com.dzics.business.service.BusinessUserService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.UserListRes;
import com.dzics.common.util.PageLimit;
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
 * Date 2021/1/6.
 */
@Api(tags = {"用户管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/account")
@Controller
public class UserAccountController {

    @Autowired
    private BusinessUserService businessUserService;


//    @OperLog(operModul = "用户管理", operType = OperType.QUERY, operDesc = "账号列表", operatorType = "后台")
    @ApiOperation(value = "账号列表")
    @ApiOperationSupport(author = "NeverEnd", order = 130)
    @GetMapping(value = "/user")
    public Result<UserListRes> userLists(PageLimit pageLimit, SelUser selUser,
                                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = businessUserService.userLists(pageLimit, selUser, sub);
        return result;
    }


    @OperLog(operModul = "用户管理", operType = OperType.ADD, operDesc = "新增账号", operatorType = "后台")
    @ApiOperation(value = "新增账号")
    @ApiOperationSupport(author = "NeverEnd", order = 130)
    @PostMapping(value = "/user")
    public Result addUser(@Valid @RequestBody RegisterVo registerVo,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = businessUserService.addUser(registerVo, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.UPDATE, operDesc = "编辑账号", operatorType = "后台")
    @ApiOperation(value = "编辑账号")
    @ApiOperationSupport(author = "NeverEnd", order = 131)
    @PutMapping(value = "/user")
    public Result delUser(@Valid @RequestBody UpdateUser updateUser,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = businessUserService.updateUser(updateUser, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.UPDATE, operDesc = "禁用启用账号", operatorType = "后台")
    @ApiOperation(value = "禁用启用账号")
    @ApiOperationSupport(author = "NeverEnd", order = 132)
    @PutMapping(value = "/disable/enabled/user")
    public Result disableEnabledUser(@Valid @RequestBody DisableEnabledUser disableEnabledUser,
                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = businessUserService.disableEnabledUser(disableEnabledUser, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.UPDATE, operDesc = "重置密码", operatorType = "后台")
    @ApiOperation(value = "重置密码")
    @ApiOperationSupport(author = "NeverEnd", order = 133)
    @PutMapping(value = "/reset/user")
    public Result resetUser(@Valid @RequestBody ResetUser resetUser,
                            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = businessUserService.resetUser(resetUser, sub);
        return result;
    }

    @OperLog(operModul = "用户管理", operType = OperType.DEL, operDesc = "删除账号", operatorType = "后台")
    @ApiOperation(value = "删除账号")
    @ApiOperationSupport(author = "NeverEnd", order = 134)
    @DeleteMapping(value = "/user/{userId}/{usernum}")
    public Result delUser(@PathVariable("userId") String userId,
                          @PathVariable("usernum") String usernum,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result result = businessUserService.delUser(Long.valueOf(userId), usernum, sub);
        return result;
    }


//    @OperLog(operModul = "用户管理", operType = OperType.QUERY, operDesc = "添加用户用户参数信息", operatorType = "后台")
    @ApiOperation(value = "添加用户用户参数信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 110)
    @GetMapping(value = "/role")
    public Result getRoles(@RequestParam(value = "userId", required = false) Long userId,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.getRolesNotAdmin(sub, userId);
    }


}
