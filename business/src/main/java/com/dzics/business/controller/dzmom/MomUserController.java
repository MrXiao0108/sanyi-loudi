package com.dzics.business.controller.dzmom;

import com.dzics.business.service.BusMomOrderService;
import com.dzics.common.model.entity.MomUser;
import com.dzics.common.model.request.mom.PutMomOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomUserService;
import com.dzics.common.util.PageLimitBase;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2022/1/10.
 * @since
 */
@Api(tags = {"MOM人员信息"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/mom/user")
@RestController
public class MomUserController {

    @Autowired
    private MomUserService momUserService;
    @Autowired
    private BusMomOrderService busMomOrderService;


    @ApiOperationSupport(author = "NeverEnd", order = 1)
    @ApiOperation(value = "MOM人员信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public Result<MomUser> getMomUser(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                      PageLimitBase limit) {
        return momUserService.getMomUser(limit);
    }


    @ApiOperation(value = "订单开始按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PutMapping("/start/order")
    public Result MomUserBeginOrder(@RequestBody @Valid PutMomOrder putMomOrder) {
        return busMomOrderService.MomorderBegin(putMomOrder);
    }

    @ApiOperation(value = "关闭订单按钮", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 8)
    @PutMapping("/close/order")
    public Result MomUserStopOrder(@RequestBody @Valid PutMomOrder putMomOrder) {
        return busMomOrderService.MomorderClose(putMomOrder);
    }

}
