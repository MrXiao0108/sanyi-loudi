package com.dzics.business.controller.download.workhistory;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.JobExecutionLogService;
import com.dzics.business.service.JobStatusTraceLogService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.workhistory.GetWorkStatusVo;
import com.dzics.common.model.request.workhistory.GetWorkVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.workhistory.GetWorkDo;
import com.dzics.common.model.response.workhistory.GetWorkStatusDo;
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
 * @author xnb
 * @date 2021年11月22日 15:21
 */
@Api(tags = {"Excel历史作业"})
@RestController
@RequestMapping(value = "/exp/tool/workHistory", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class ExcelWorkController {
    @Autowired
    private JobExecutionLogService jobExecutionLogService;
    @Autowired
    private JobStatusTraceLogService jobStatusTraceLogService;

    @ApiOperation(value = "历史轨迹", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/trajectory", method = RequestMethod.GET)
    public void getTrajectory(HttpServletResponse response,
                              @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,GetWorkVo getWorkVo) throws IOException {
        String fileNameBase = "历史作业轨迹管理报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            getWorkVo.setPage(-1);
            Result<List<GetWorkDo>> list = jobExecutionLogService.getList(getWorkVo);
            EasyExcel.write(response.getOutputStream(), GetWorkDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(list.getData());
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "历史状态", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public void getStatus(HttpServletResponse response, GetWorkStatusVo getWorkStatusVo,
                          @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                          @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "历史作业状态管理报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            getWorkStatusVo.setPage(-1);
            Result<List<GetWorkStatusDo>> list = jobStatusTraceLogService.getList(getWorkStatusVo);
            EasyExcel.write(response.getOutputStream(), GetWorkStatusDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(list.getData());
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }


    }


}
