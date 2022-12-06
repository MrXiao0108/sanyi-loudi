package com.dzics.business.controller.download.lineplan;


import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.BusinessEquipmentProNumService;
import com.dzics.business.service.BusinessProductionPlanDayService;
import com.dzics.business.service.BusinessProductionPlanService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.constant.FinalCode;
import com.dzics.common.model.request.plan.SelectEquipmentProductionVo;
import com.dzics.common.model.request.plan.SelectProductionDetailsVo;
import com.dzics.common.model.request.plan.SelectProductionPlanRecordVo;
import com.dzics.common.model.request.plan.SelectProductionPlanVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.plan.ProductionPlanDo;
import com.dzics.common.model.response.plan.SelectEquipmentProductionDo;
import com.dzics.common.model.response.plan.SelectProductionDetailsDo;
import com.dzics.common.model.response.plan.SelectProductionPlanRecordDo;
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
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

@Api(tags = {"生产计划管理导出"})
@RestController
@Slf4j
@RequestMapping(value="/line/pan/excel", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
public class LinePlanExcelController {
    @Autowired
    BusinessProductionPlanService businessProductionPlanService;
    @Autowired
    BusinessProductionPlanDayService businessProductionPlanDayService;
    @Autowired
    BusinessEquipmentProNumService businessEquipmentProNumService;

    @ApiOperation(value = "查询产线日生产计划列表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/dayPlan")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                         PageLimit pageLimit, SelectProductionPlanVo selectProductionPlanVo, HttpServletResponse response) throws IOException {
        selectProductionPlanVo.setPlanType(FinalCode.DZ_PLAN_DAY);

        String fileNameBase = "生产计划设置";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<ProductionPlanDo>> result = businessProductionPlanService.list(sub,pageLimit,selectProductionPlanVo);
            List<ProductionPlanDo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), ProductionPlanDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }

    }

    @ApiOperation(value = "产线日生产计划记录导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/dayPlanData")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,HttpServletResponse response,
                                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                     PageLimit pageLimit, SelectProductionPlanRecordVo selectProductionPlanRecordVo) throws IOException {

        String fileNameBase = "产线日生产计划记录";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SelectProductionPlanRecordDo>> result = businessProductionPlanDayService.list(sub,pageLimit,selectProductionPlanRecordVo);
            List<SelectProductionPlanRecordDo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SelectProductionPlanRecordDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }

    }
    @ApiOperation(value = "产品生产明细列表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/product/data/details")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                  PageLimit pageLimit, SelectProductionDetailsVo selectProductionDetailsVo,HttpServletResponse response
    ) throws IOException {
        String fileNameBase = "产品生产明细列表";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SelectProductionDetailsDo>> result =businessEquipmentProNumService.list(sub, pageLimit, selectProductionDetailsVo);
            List<SelectProductionDetailsDo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SelectProductionDetailsDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

    @ApiOperation(value = "设备生产数量明细列表导出", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 111)
    @GetMapping("/equipment/data/details")
    public void list(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                    PageLimit pageLimit, SelectEquipmentProductionVo selectProductionDetailsVo,HttpServletResponse response
    ) throws IOException {
        String fileNameBase = "设备生产数量明细列表";
        try {
            pageLimit.setPage(1);
            pageLimit.setLimit(FinalCode.SELECT_SUM_EXCEL);
            Result<List<SelectEquipmentProductionDo>> result =businessEquipmentProNumService.listProductionEquipment(sub,pageLimit,selectProductionDetailsVo);
            List<SelectEquipmentProductionDo> data = result.getData();
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            EasyExcel.write(response.getOutputStream(), SelectEquipmentProductionDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        }catch (Exception e){
            log.error("导出{}异常：{}", fileNameBase, e.getMessage(), e);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
