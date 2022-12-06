package com.dzics.kanban.controller.system;

import com.dzics.kanban.common.annotation.OperLog;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.model.response.MenusInfo;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.model.vo.rolemenu.AddPermission;
import com.dzics.kanban.model.vo.rolemenu.UpdatePermission;
import com.dzics.kanban.service.BusinessUserService;
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
 * Date 2021/1/8.
 */
@Api(tags = {"采单管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user")
@Controller
public class MenuController {
    @Autowired
    private BusinessUserService businessUserService;


//    @OperLog(operModul = "采单管理", operType = OperType.QUERY, operDesc = "采单列表", operatorType = "后台")
    @ApiOperation(value = "采单列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu")
    public Result<MenusInfo> selMenuPermission(
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.selMenuPermission(sub);
    }

    @OperLog(operModul = "采单管理", operType = OperType.ADD, operDesc = "新增采单", operatorType = "后台")
    @ApiOperation(value = "新增采单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping(value = "/menu")
    public Result addPermission(
            @Valid @RequestBody AddPermission addPermission,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.addPermission(addPermission, sub);
    }


//    @OperLog(operModul = "采单管理", operType = OperType.QUERY, operDesc = "采单列表", operatorType = "后台")
    @ApiOperation(value = "采单详情", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/menu/{id}")
    public Result<MenusInfo> selMenuPermission(@PathVariable("id") Long id,
                                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.selMenuPermissionId(id,sub);
    }

    @OperLog(operModul = "采单管理", operType = OperType.UPDATE, operDesc = "修改采单", operatorType = "后台")
    @ApiOperation(value = "修改采单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping(value = "/menu")
    public Result updatePermission(@Valid @RequestBody UpdatePermission updatePermission,
                                   @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                   @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.updatePermission(updatePermission, sub);
    }

    @OperLog(operModul = "采单管理", operType = OperType.DEL, operDesc = "删除采单", operatorType = "后台")
    @ApiOperation(value = "删除采单", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping(value = "/menu/{id}")
    public Result delPermission(@PathVariable("id") Long id,
                                @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessUserService.delPermission(id, sub);
    }
}
