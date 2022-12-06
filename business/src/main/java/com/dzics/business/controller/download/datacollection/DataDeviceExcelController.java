package com.dzics.business.controller.download.datacollection;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.DataDeviceService;
import com.dzics.common.enums.OperType;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzDataDevice;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.datadevice.GetDataDeviceVo;
import com.dzics.common.model.response.Result;
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

@Api(tags = {"三一设备管理导出"})
@RestController
@Slf4j
@RequestMapping(value="/collection/dzDataDevice/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class DataDeviceExcelController {

    @Autowired
    private DataDeviceService dataDeviceService;

    @OperLog(operModul = "三一设备管理", operType = OperType.QUERY, operDesc = "查询设备列表", operatorType = "后台")
    @ApiOperation(value = "查询设备列表")
    @GetMapping
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                     GetDataDeviceVo dataDeviceVo, HttpServletResponse response) throws IOException {

        String fileNameBase = "三一设备管理";
        try {
            dataDeviceVo.setPage(1);
            dataDeviceVo.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<DzDataDevice>> result = dataDeviceService.list(dataDeviceVo);
            List<DzDataDevice> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), DzDataDevice.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
