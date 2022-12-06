package com.dzics.sanymom.controller;

import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.UserLoginMessage;
import com.dzics.sanymom.service.MomUserMessage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Api(tags = {"账户登录"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/mom/user")
@Slf4j
@CrossOrigin
public class MomAuthUserController {

    @Autowired
    private MomUserMessage momUserService;


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "登录信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/messgae")
    public Result<UserLoginMessage> getUserMsg() {
        return momUserService.getUserMessage();
    }


}

