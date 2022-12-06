package com.dzics.kanban.controller.authorlogin;

import com.dzics.kanban.common.annotation.OperLog;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.exception.CustomException;
import com.dzics.kanban.exception.CustomWarnException;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;
import com.dzics.kanban.framework.auth.JwtAuthService;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.model.response.*;
import com.dzics.kanban.model.vo.user.SwitchSite;
import com.dzics.kanban.service.BusiSysLoginLogService;
import com.dzics.kanban.service.BusinessUserService;
import com.dzics.kanban.service.SysUserServiceDao;
import com.dzics.kanban.util.IPUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.DynamicParameter;
import com.github.xiaoymin.knife4j.annotations.DynamicParameters;
import eu.bitwalker.useragentutils.UserAgent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/1/8.
 */
@Api(tags = {"账号授权"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/auth")
@Controller
@Slf4j
public class AuthController {
    @Autowired
    private SysUserServiceDao sysUserServiceDao;
    @Autowired
    private BusinessUserService businessUserService;
    @Autowired
    private JwtAuthService jwtAuthService;
    @Autowired
    private BusiSysLoginLogService busiSysLoginLogService;

    @ApiOperation(value = "账号授权", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 101, params = @DynamicParameters(name = "userObj", properties = {
            @DynamicParameter(name = "username", value = "手机号码", example = "admin", required = true, dataTypeClass = String.class),
            @DynamicParameter(name = "password", value = "密码为加密后的密码", example = "admin", required = true, dataTypeClass = String.class)
    }))
    @PostMapping(value = "/login")
    public Result<UserTokenMsg> login(HttpServletRequest request, @RequestBody Map<String, String> userObj) {
        String username = userObj.get("username");
        String password = userObj.get("password");
        UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
        String ipAddr = IPUtil.getIpAddress(request);
        String orgCode = "";
        try {
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, CustomResponseCode.ERR1);
            }
            UserTokenMsg login = jwtAuthService.login(username, password);
            SysUser byUserName = sysUserServiceDao.getByUserName(username);
            orgCode = byUserName.getCode();
            Result depart = businessUserService.querySwitchSite(byUserName, false);
            Map<String, Object> map = new HashMap<>();
            map.put("userTokenMsg", login);
            map.put("departs", depart.getData());
            busiSysLoginLogService.saveLogin(userAgent, ipAddr, login, userObj, null,orgCode);
            return new Result(CustomExceptionType.OK, map);
        } catch (Exception e) {
            log.error("登录错误：error：{}", e);
            busiSysLoginLogService.saveLogin(userAgent, ipAddr, null, userObj, e,orgCode);
            if (e instanceof SQLException) {
                return new Result(CustomExceptionType.SYSTEM_ERROR);
            } else if (e instanceof CustomException || e instanceof CustomWarnException) {
                return new Result(CustomExceptionType.SYSTEM_ERROR, e.getMessage());
            } else {
                return new Result(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
            }
        }
    }


    //    @OperLog(operModul = "账号授权", operType = OperType.OTHER, operDesc = "刷新token")
    @ApiOperationSupport(author = "NeverEnd", order = 202)
    @ApiOperation(value = "刷新token", notes = "刷新token", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/refreshtoken")
    public Result<UserTokenMsg> refresh(@RequestHeader(value = "refToken", required = false) @ApiParam(value = "刷新token", required = true) String refToken,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "登录账号", required = true) String sub) {
        return jwtAuthService.refreshToken(refToken, sub);
    }

    @OperLog(operModul = "账号授权", operType = OperType.LOOUT, operDesc = "退出登录")
    @ApiOperationSupport(author = "NeverEnd", order = 203)
    @ApiOperation(value = "退出登录", notes = "用户退出登录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping(value = "/signout")
    public Result logout(@RequestHeader(value = "sub", required = false) @ApiParam(value = "登录账号", required = true) String sub,
                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String jwtToken) {
        return jwtAuthService.signout(sub);
    }


    //    @OperLog(operModul = "账号授权", operType = OperType.QUERY, operDesc = "获取用户信息", operatorType = "后台")
    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 102)
    @GetMapping(value = "/getInfo")
    public Result<UserInfo> getInfo(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {

        return businessUserService.getInfo(sub);
    }


    //    @OperLog(operModul = "账号授权", operType = OperType.QUERY, operDesc = "获取用户路由列表", operatorType = "后台")
    @ApiOperation(value = "获取用户路由列表", notes = "获取用户路由列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 103)
    @GetMapping(value = "/getRouters")
    public Result<RoutersInfo> getRouters(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.getRouters(sub);
    }

    //    @OperLog(operModul = "账号授权", operType = OperType.UPDATE, operDesc = "切换站点", operatorType = "后台")
    @ApiOperationSupport(author = "NeverEnd", order = 203)
    @ApiOperation(value = "切换站点", notes = "切换站点后需重新加载路由", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/switch/site")
    public Result switchSite(@RequestBody @Valid SwitchSite switchSite, @RequestHeader(value = "sub", required = false) @ApiParam(value = "登录账号", required = true) String sub,
                             @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String jwtToken) {
        return businessUserService.switchSite(switchSite, sub);
    }

    //    @OperLog(operModul = "账号授权", operType = OperType.QUERY, operDesc = "查看可切换站点", operatorType = "后台")
    @ApiOperationSupport(author = "NeverEnd", order = 203)
    @ApiOperation(value = "可切换站点", notes = "可切的换站点", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/you/can/switch/site")
    public Result<SwitchSiteDo> querySwitchSite(@RequestParam(value = "isAll", defaultValue = "true", required = false) Boolean isAll, @RequestHeader(value = "sub", required = false) @ApiParam(value = "登录账号", required = true) String sub,
                                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String jwtToken) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        return businessUserService.querySwitchSite(byUserName, isAll);
    }
}
