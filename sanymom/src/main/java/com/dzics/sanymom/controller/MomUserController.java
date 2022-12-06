package com.dzics.sanymom.controller;

import com.dzics.common.model.request.mom.MomUserLogin;
import com.dzics.common.model.request.mom.OperationOrderVo;
import com.dzics.common.model.response.Result;
import com.dzics.sanymom.service.MomUserMessage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@CrossOrigin
@Api(tags = {"账户登录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/mom/login")
@Slf4j
public class MomUserController {


    @Autowired
    private MomUserMessage momUserService;


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "当前产线", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/line")
    public Result getUseLineMsg() {
        return momUserService.getUseLineMsg();
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "是否登录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/login/isok")
    public Result getUseLineIslogin() {
        return momUserService.getUseLineIslogin();
    }

    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "MOM登录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping("/user")
    public Result login(@Valid @RequestBody MomUserLogin login) {
        return momUserService.login(login);
    }


    @ApiOperationSupport(author = "xnb",order = 1)
    @ApiOperation(value = "订单操作",consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping
    public Result operationOrder(@Valid @RequestBody OperationOrderVo orderVo){ return momUserService.operationOrder(orderVo); }


}

