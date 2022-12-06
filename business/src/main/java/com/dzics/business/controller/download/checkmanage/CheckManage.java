package com.dzics.business.controller.download.checkmanage;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.WriteTable;
import com.alibaba.fastjson.JSON;
import com.dzics.business.service.ProductTrendChartService;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.request.SelectTrendChartVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.cpk.*;
import com.dzics.common.model.response.down.*;
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
import javax.validation.Valid;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 检测管理Excel
 *
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Api(tags = {"Excel检测管理"})
@RestController
@RequestMapping(value = "/exp/check/manage", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class CheckManage {
    @Autowired
    private ProductTrendChartService productTrendChartService;

    @ApiOperation(value = "检测走势图", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/trend", method = RequestMethod.GET)
    public void list(HttpServletResponse response,
                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                     @Valid SelectTrendChartVo selectTrendChartVo, ExpCpkChart expCpkChart) throws IOException {

        String fileNameBase = "检测走势图报表";
        ExcelWriter write = null;
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            Result<AnalysisDataVO> list = productTrendChartService.list(sub, selectTrendChartVo);
            AnalysisDataVO data = list.getData();
            if (data != null) {
                ExpCpkAll expCpkAll = productTrendChartService.getExpCpkDataAll(data);
                List<ExpCpkData> expCpkData = expCpkAll.getExpCpkData();
                List<ExpCpkOne> expCpkOnes = expCpkAll.getExpCpkOnes();
                List<CPKA> cpkas = expCpkAll.getCpkas();
                List<CPKB> cpkbs = expCpkAll.getCpkbs();
                List<CPKC> cpkcs = expCpkAll.getCpkcs();
                List<CPKD> cpkds = expCpkAll.getCpkds();
                List<ExpCpkInfo> info = expCpkAll.getInfo();
                List<ExpCpkImg> imgs = new ArrayList<>();
                ExpCpkImg cpkImg = new ExpCpkImg();
                cpkImg.setUrlLineObj(new URL(expCpkChart.getLineUrl()));
                cpkImg.setCpkUrlObj(new URL(expCpkChart.getCpkUrl()));
                imgs.add(cpkImg);
                write = EasyExcel.write(response.getOutputStream(), ExpCpkData.class).build();
                WriteSheet writeSheet = EasyExcel.writerSheet(fileNameBase).needHead(Boolean.FALSE).build();
                WriteTable writeTable0 = EasyExcel.writerTable(0).head(ExpCpkOne.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable11 = EasyExcel.writerTable(1).head(CPKA.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable12 = EasyExcel.writerTable(2).head(CPKB.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable13 = EasyExcel.writerTable(3).head(CPKC.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable14 = EasyExcel.writerTable(4).head(CPKD.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable2 = EasyExcel.writerTable(5).head(ExpCpkInfo.class).needHead(Boolean.FALSE).build();
                WriteTable writeTable3 = EasyExcel.writerTable(6).head(ExpCpkImg.class).needHead(Boolean.TRUE).build();
                WriteTable writeTable4 = EasyExcel.writerTable(7).head(ExpCpkData.class).needHead(Boolean.TRUE).build();
                write.write(expCpkOnes, writeSheet, writeTable0);
                write.write(cpkas, writeSheet, writeTable11);
                write.write(cpkbs, writeSheet, writeTable12);
                write.write(cpkcs, writeSheet, writeTable13);
                write.write(cpkds, writeSheet, writeTable14);
                write.write(info, writeSheet, writeTable2);
                write.write(imgs, writeSheet, writeTable3);
                write.write(expCpkData, writeSheet, writeTable4);
            } else {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR17);
            }
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        } finally {
            if (write != null) {
                write.finish();
            }
        }
    }



}
