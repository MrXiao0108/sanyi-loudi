package com.dzics.business.controller.download.logs;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.model.response.proddetection.HeaderClom;
import com.dzics.business.model.response.proddetection.TcpLogProDetection;
import com.dzics.business.model.vo.CommuLogPrm;
import com.dzics.business.service.BusCommunicationLogService;
import com.dzics.business.service.BusiLoginLogService;
import com.dzics.business.service.BusiOperationLog;
import com.dzics.business.service.BusinessEquipmentStateLogService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzEquipmentStateLog;
import com.dzics.common.model.entity.SysCommunicationLog;
import com.dzics.common.model.entity.SysLoginLog;
import com.dzics.common.model.entity.SysOperationLogging;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.SelectEquipmentStateVo;
import com.dzics.common.model.request.SysOperationLoggingVo;
import com.dzics.common.model.request.SysloginVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
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
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Api(tags = {"日志管理导出"})
@RestController
@Slf4j
@RequestMapping(value="/log/reportwork", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class LogExcelController {
    @Autowired
    private BusCommunicationLogService busCommunicationLogService;
    @Autowired
    BusinessEquipmentStateLogService businessEquipmentStateLogService;
    @Autowired
    private BusiOperationLog busiOperationLog;
    @Autowired
    private BusiLoginLogService loginLogService;

    @ApiOperation(value = "通信日志导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/communication/log")
    public void communicationLog(PageLimit pageLimit, CommuLogPrm commuLogPrm, HttpServletResponse response,
                                   @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {

        String fileNameBase = "通信日志";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysCommunicationLog>> result = busCommunicationLogService.communicationLog(pageLimit, commuLogPrm);
            List<SysCommunicationLog> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysCommunicationLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @ApiOperation(value = "设备运行状态日志导出")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping
    public  void list(PageLimit pageLimit, @Valid SelectEquipmentStateVo selectEquipmentStateVo, HttpServletResponse response,
                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) throws IOException {

        String fileNameBase = "设备运行状态日志";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<DzEquipmentStateLog>> result = businessEquipmentStateLogService.list(sub,pageLimit,selectEquipmentStateVo);
            List<DzEquipmentStateLog> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzEquipmentStateLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @ApiOperation(value = "登录日志导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/log/login")
    public void queryLogin(
            PageLimit pageLimit, SysloginVo sysloginVo,HttpServletResponse response,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
            @RequestHeader(value = "code", required = false) @ApiParam(value = "用户账号", required = true) String code) throws IOException {

        String fileNameBase = "登录日志";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysLoginLog>> result = loginLogService.queryLogin(pageLimit, sysloginVo, sub, code);
            List<SysLoginLog> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysLoginLog.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }


    }

    @ApiOperation(value = "操作日志导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/log")
    public void queryOperLog(
            PageLimit pageLimit, SysOperationLoggingVo sysOperationLoggingVo,HttpServletResponse response,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
            @RequestHeader(value = "code", required = false) @ApiParam(value = "用户账号", required = true) String code) throws IOException {

        String fileNameBase = "操作日志";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SysOperationLogging>> result = busiOperationLog.queryOperLog(pageLimit, sub, code, sysOperationLoggingVo);
            List<SysOperationLogging> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SysOperationLogging.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }






    @ApiOperation(value = "通信日志指令导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/communication/tcp/log")
    public void communicationLogTcp(PageLimit pageLimit, CommuLogPrm commuLogPrm,HttpServletResponse response,
                                      @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        pageLimit.setLimit(10000);
        Result<TcpLogProDetection>result= busCommunicationLogService.communicationLogTcp(pageLimit, commuLogPrm);
        String fileNameBase="通信日志指令";
        TcpLogProDetection data = result.getData();
        List<HeaderClom> tableColumn = data.getTableColumn();
        List<Map<String, Object>> tableData = data.getTableData();

        List<List<String>>headerList= new ArrayList<>();//头
        List<List<String>>dataList=new ArrayList<>();//数据
        for (HeaderClom headerClom:tableColumn) {
            List<String>head=new ArrayList<>();
            head.add(headerClom.getColName());
            headerList.add(head);
        }
        for (Map<String, Object> map:tableData) {
            List<String>itemData=new ArrayList<>();
            for (HeaderClom headerClom:tableColumn) {
                String colData = headerClom.getColData();
                String s = map.get(colData).toString();
                itemData.add(s);
            }
            dataList.add(itemData);
        }
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
        String fileName = URLEncoder.encode(fileNameBase+"-" + System.currentTimeMillis()+ ".xlsx", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename=" + fileName);
        EasyExcel.write(response.getOutputStream()).registerWriteHandler(new CustomCellWriteHandler()).head(headerList).sheet(fileName).doWrite(dataList);

    }

}
