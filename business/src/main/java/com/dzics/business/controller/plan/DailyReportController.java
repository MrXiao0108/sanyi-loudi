package com.dzics.business.controller.plan;

import com.alibaba.excel.EasyExcel;
import com.dzics.business.service.BusDayDailyServcie;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.DayDailyReportExcel;
import com.dzics.common.util.DateUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * 日产报表
 *
 * @author ZhangChengJun
 * Date 2021/6/23.
 */
@Api(tags = {"日产报表"})
@RestController
@RequestMapping(value = "/daily/report")
public class DailyReportController {
    @Autowired
    private BusDayDailyServcie busDayDailyServcie;
    @Autowired
    private DateUtil dateUtil;

    @ApiOperation(value = "日报表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping
    public Result dayDailyReport(BaseTimeLimit timeLimit,
                                 @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDayDailyServcie.dayDailyReport(sub, timeLimit);
    }


}
