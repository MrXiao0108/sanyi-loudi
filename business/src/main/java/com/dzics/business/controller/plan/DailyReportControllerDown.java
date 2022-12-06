package com.dzics.business.controller.plan;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.service.BusDayDailyServcie;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.DayDailyReportExcel;
import com.dzics.common.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 日产报表
 *
 * @author ZhangChengJun
 * Date 2021/6/23.
 */
@Api(tags = {"日产报表"})
@RestController
@RequestMapping(value = "/daily/report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class DailyReportControllerDown {
    @Autowired
    private BusDayDailyServcie busDayDailyServcie;
    @Autowired
    private DateUtil dateUtil;


    @ApiOperation(value = "日报表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/order/file", method = RequestMethod.GET)
    public void download(HttpServletResponse response, BaseTimeLimit timeLimit,
                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        try {
            String fileName = URLEncoder.encode("日产数量报表-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            timeLimit.setPage(-1);
            Result<List<DayDailyReportExcel>> listResult = busDayDailyServcie.dayDailyReport(sub, timeLimit);
            List<DayDailyReportExcel> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), DayDailyReportExcel.class).sheet("日产数量报表").doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出日生产数量异常：{}", throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

}
