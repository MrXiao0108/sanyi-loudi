package com.dzics.business.controller.download.line;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.BusinessDzProductionLineService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.line.LineParmsList;
import com.dzics.common.model.response.LineDo;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(tags = {"产线管理导出"})
@RestController
@Slf4j
@RequestMapping(value="/line/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class LineExcelController {

    @Autowired
    BusinessDzProductionLineService lineService;

    @ApiOperation(value = "产线列表导出")
    @ApiOperationSupport(author = "jq", order = 1)
    @GetMapping
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub, HttpServletResponse response,
                               LineParmsList lineParmsList) throws IOException {
        String fileNameBase = "产线列表";
        try {
            lineParmsList.setPage(1);
            lineParmsList.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<LineDo>> result = lineService.list(sub, lineParmsList);
            List<LineDo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), LineDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
