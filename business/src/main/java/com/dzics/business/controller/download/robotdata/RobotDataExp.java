package com.dzics.business.controller.download.robotdata;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.BusinessEquipmentService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.SelectEquipmentDataVo;
import com.dzics.common.model.request.SelectEquipmentVo;
import com.dzics.common.model.response.EquipmentDataDo;
import com.dzics.common.model.response.EquipmentDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;
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

/**
 * 机器人管理数据导出
 *
 * @author ZhangChengJun
 * Date 2021/7/6.
 * @since
 */
@Api(tags = {"Excel机器人数据管理"})
@RestController
@RequestMapping(value = "/exp/robot", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class RobotDataExp {

    @Autowired
    private BusinessEquipmentService dzEquipmentService;


    @ApiOperation(value = "机器人历史数据表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/historical/data", method = RequestMethod.GET)
    public void listEquipmentData(HttpServletResponse response, SelectEquipmentDataVo selectEquipmentDataVo,
                                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub
    ) throws IOException {

        String fileNameBase = "机器人历史数据报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            selectEquipmentDataVo.setPage(-1);
            Result<List<EquipmentDataDo>> listResult = dzEquipmentService.listEquipmentData(sub, FinalCode.ROBOT_EQUIPMENT_CODE, selectEquipmentDataVo);
            List<EquipmentDataDo> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), EquipmentDataDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }

    }



    @ApiOperation(value = "机器人停机数据报表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/shutdown/data", method = RequestMethod.GET)
    public void list(HttpServletResponse response,
                                    PageLimit pageLimit, SelectEquipmentVo selectEquipmentVo,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {


        String fileNameBase = "机器人停机数据报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            pageLimit.setPage(-1);
            Result<List<EquipmentDo>> listResult = dzEquipmentService.list(sub, FinalCode.ROBOT_EQUIPMENT_CODE, pageLimit, selectEquipmentVo);
            List<EquipmentDo> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), EquipmentDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }

    }


}
