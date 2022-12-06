package com.dzics.business.controller.download.equipment;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.*;
import com.dzics.common.enums.OperType;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.SelectEquipmentVo;
import com.dzics.common.model.request.base.BaseTimeLimit;
import com.dzics.common.model.request.device.FaultRecordParmsReq;
import com.dzics.common.model.request.device.maintain.MaintainDeviceParms;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.EquipmentListDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.device.FaultRecord;
import com.dzics.common.model.response.device.maintain.MaintainDevice;
import com.dzics.common.model.response.devicecheck.GetDeviceCheckDo;
import com.dzics.common.util.PageLimitBase;
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

@Api(tags = {"设备管理导出"})
@RestController
@Slf4j
@RequestMapping(value="/equipment/reportwork", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class EquipmentExcelController {

    @Autowired
    private BusinessEquipmentService businessEquipmentService;

    @Autowired
    private BusDzRepairHistoryService dzRepairHistoryService;

    @Autowired
    private BusMaintainDeviceService busMaintainDeviceService;

    @Autowired
    private CheckHistoryService checkHistoryService;

    @Autowired
    private CheckUpItemService checkUpItemService;

    @OperLog(operModul = "设备管理相关", operType = OperType.QUERY, operDesc = "设备列表", operatorType = "后台")
    @ApiOperation(value = "设备列表")
    @ApiOperationSupport(author = "jq", order = 2)
    @GetMapping
    public void list(SelectEquipmentVo selectEquipmentVo, HttpServletResponse response,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) throws IOException {

        String fileNameBase = "设备列表";
        try {
            selectEquipmentVo.setPage(1);
            selectEquipmentVo.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<EquipmentListDo>> result = businessEquipmentService.list(sub,selectEquipmentVo);
            List<EquipmentListDo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), EquipmentListDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @OperLog(operModul = "设备管理相关", operType = OperType.QUERY, operDesc = "设备故障台账", operatorType = "后台")
    @ApiOperation(value = "设备故障台账")
    @ApiOperationSupport(author = "xnb", order = 111)
    @GetMapping("/fault")
    public void excelFault(FaultRecordParmsReq parsReq,PageLimitBase pageLimit, HttpServletResponse response,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "设备故障台账";
        try {
            pageLimit.setPage(-1);
            Result<List<FaultRecord>>result = dzRepairHistoryService.getFaultRecordList(sub,pageLimit, parsReq);
            List<FaultRecord> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), FaultRecord.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @OperLog(operModul = "设备管理相关", operType = OperType.QUERY, operDesc = "设备保养记录", operatorType = "后台")
    @ApiOperation(value = "设备保养记录")
    @ApiOperationSupport(author = "xnb", order = 222)
    @GetMapping("/maintain")
    public void maintain(BaseTimeLimit pageLimit, MaintainDeviceParms parmsReq, HttpServletResponse response,
                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "设备保养记录";
        try {
            pageLimit.setPage(-1);
            Result<List<MaintainDevice>>result = busMaintainDeviceService.getMaintainList(sub, pageLimit, parmsReq);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), MaintainDevice.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(result.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @OperLog(operModul = "设备管理相关", operType = OperType.QUERY, operDesc = "设备巡检记录管理", operatorType = "后台")
    @ApiOperation(value = "设备巡检记录管理")
    @ApiOperationSupport(author = "xnb", order = 333)
    @GetMapping("/Inspection")
    public void Inspection(GetDeviceCheckVo getDeviceCheckVo, HttpServletResponse response,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "设备巡检记录管理";
        try {
            getDeviceCheckVo.setPage(-1);
            Result<List<GetDeviceCheckDo>>result = checkHistoryService.list(getDeviceCheckVo);
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), GetDeviceCheckDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(result.getData());
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


//    @OperLog(operModul = "设备管理相关", operType = OperType.QUERY, operDesc = "巡检项管理", operatorType = "后台")
//    @ApiOperation(value = "巡检项管理")
//    @ApiOperationSupport(author = "xnb", order = 444)
//    @GetMapping("/InspectionTerm")
//    public void InspectionTerm(HttpServletResponse response, PageLimit pageLimit, Integer deviceType, String checkName,
//                               @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
//                               @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
//        String fileNameBase = "巡检项管理";
//        try {
//            pageLimit.setPage(-1);
//            Result<List<CheckTypeDo>>result = checkUpItemService.list(pageLimit, deviceType, checkName);
//            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
//            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
//            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
//            EasyExcel.write(response.getOutputStream(), CheckTypeDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(result.getData());
//        }catch (Exception e){
//            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
//            response.setCharacterEncoding("utf-8");
//            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
//            response.getWriter().println(JSON.toJSONString(error));
//        }
//    }
}
