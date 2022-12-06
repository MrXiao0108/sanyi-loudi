package com.dzics.business.controller.download.reportwork;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.model.vo.productiontask.station.SelWorkStation;
import com.dzics.business.model.vo.productiontask.workingprocedure.WorkingProcedureAdd;
import com.dzics.business.service.BusStationManageService;
import com.dzics.business.service.MomDzWorkingFlowService;
import com.dzics.business.service.WorkingProcedureService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.mom.GetWorkingDetailsVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetWorkingDetailsDo;
import com.dzics.common.model.response.productiontask.station.ResWorkStation;
import com.dzics.common.model.response.productiontask.workingProcedure.WorkingProcedureRes;
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
 * 生产任务报工管理
 *
 * @author ZhangChengJun
 * Date 2021/7/6.
 * @since
 */
@Api(tags = {"Excel生产任务报工管理"})
@RestController
@RequestMapping(value = "/exp/reportwork", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class ReportWorkDownlod {

    @Autowired
    private WorkingProcedureService workingProcedureService;

    @Autowired
    private BusStationManageService busStationManageService;


    @Autowired
    private MomDzWorkingFlowService momDzWorkingFlowService;

    @ApiOperation(value = "工序管理", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/working/procedure", method = RequestMethod.GET)
    public void expWorkingProcedure(HttpServletResponse response,
                                    PageLimit timeLimit, WorkingProcedureAdd procedureAdd,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "工序报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            timeLimit.setPage(-1);
            Result<List<WorkingProcedureRes>> listResult = workingProcedureService.selWorkingProcedure(timeLimit, procedureAdd, sub);
            List<WorkingProcedureRes> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), WorkingProcedureRes.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "工位管理", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/station", method = RequestMethod.GET)
    public void expStation(HttpServletResponse response,
                           SelWorkStation selWorkStation,
                           @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                           @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "工位管理";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            selWorkStation.setPage(-1);
            Result<List<ResWorkStation>> listResult = busStationManageService.getWorkingStation(selWorkStation, sub);
            List<ResWorkStation> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), ResWorkStation.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "生产任务报工详情列表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/upWork")
    public void getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo,HttpServletResponse response,
                                                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {
        String fileNameBase = "生产任务报工详情";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            getWorkingDetailsVo.setPage(-1);
            Result<List<GetWorkingDetailsDo>> listResult =momDzWorkingFlowService.getWorkingDetails(getWorkingDetailsVo);
            List<GetWorkingDetailsDo> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), GetWorkingDetailsDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
