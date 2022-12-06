package com.dzics.kanban.controller.system;

import com.dzics.kanban.common.annotation.OperLog;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.framework.auth.JwtAuthService;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.model.request.PutUserPasswordVo;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.model.response.UserInfo;
import com.dzics.kanban.service.BusinessUserService;
import com.dzics.kanban.service.SysUserServiceDao;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = {"当前用户信息"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    JwtAuthService jwtAuthService;
    @Autowired
    private BusinessUserService businessUserService;

    //    @OperLog(operModul = "当前用户信息", operType = OperType.QUERY, operDesc = "查询用户信息", operatorType = "后台")
//    @ApiOperation(value = "查询用户信息")
    @GetMapping
    public Result<SysUser> getInfo(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result<UserInfo> info = businessUserService.getInfo(sub);
        return new Result(CustomExceptionType.OK, info.getData().getUser());
    }

 /*   @OperLog(operModul = "当前用户信息", operType = OperType.UPDATE, operDesc = "编辑用户信息", operatorType = "后台")
    @ApiOperation(value = "编辑用户信息")
    @PutMapping
    public Msg put(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                    @RequestBody PutUserInfoVo putUserInfoVo
    ){
        return sysUserService.put(sub,putUserInfoVo);
    }*/

    @OperLog(operModul = "当前用户信息", operType = OperType.UPDATE, operDesc = "更改用户密码", operatorType = "后台")
    @ApiOperation(value = "更改用户密码")
    @PutMapping("/password")
    public Result putPassword(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @RequestBody @Valid PutUserPasswordVo putUserInfoVo
    ) {
        Result result = businessUserService.putPassword(sub, putUserInfoVo);
        if (result == null) {
            return jwtAuthService.signout(sub);
        }
        return result;
    }

//    @OperLog(operModul = "修改用户信息", operType = OperType.UPDATE, operDesc = "更改用户头像", operatorType = "后台")
   /* @ApiOperation(value = "更改用户头像")
    @PostMapping("/avatar")
    public Msg putAvatar(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                         @RequestParam("file")  MultipartFile file
    ){
        Msg msg = sysUserService.putAvatar(sub, file);
        return msg;
    }*/
}
