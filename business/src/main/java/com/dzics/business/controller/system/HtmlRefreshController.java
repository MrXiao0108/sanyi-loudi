package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.WareHouseService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ZhangChengJun
 * Date 2021/5/24.
 * @since
 */
@Api(tags = {"触发页面刷新"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/refresh")
@Controller
public class HtmlRefreshController {
    @Autowired
    private WareHouseService wareHouseService;

    @OperLog(operModul = "触发页面刷新", operType = OperType.UPDATE, operDesc = "刷新页面", operatorType = "后台")
    @ApiOperation(value = "刷新页面")
    @ApiOperationSupport(author = "NeverEnd",order=8)
    @PostMapping
    public Result refresh(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return wareHouseService.refresh(sub);
    }
}
