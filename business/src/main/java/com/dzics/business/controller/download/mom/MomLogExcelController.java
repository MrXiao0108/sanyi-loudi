package com.dzics.business.controller.download.mom;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.mom.AgvLogParms;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.MomLogExcelDo;
import com.dzics.common.service.LogPromptMsgMomService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(tags = {"Mom日志Excel管理"})
@RestController
@RequestMapping(value = "/Mom/log/excelWork", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class MomLogExcelController {
    @Autowired
    private LogPromptMsgMomService logPromptMsgMomService;

    @ApiOperation(value = "Mom日志导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "", method = RequestMethod.GET)
    @GetMapping
    public void exportFeeding(HttpServletResponse response, AgvLogParms logParms,
                              @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "Mom交互日志管理";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            logParms.setPage(-1);
            Result<List<MomLogExcelDo>> logsExcel = logPromptMsgMomService.getMomLogsExcel(logParms);
            EasyExcel.write(response.getOutputStream(),MomLogExcelDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(logsExcel.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
