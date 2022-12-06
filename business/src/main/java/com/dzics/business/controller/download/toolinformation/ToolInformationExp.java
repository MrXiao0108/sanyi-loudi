package com.dzics.business.controller.download.toolinformation;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.model.response.ToolconfigurationExp;
import com.dzics.business.service.BusinessToolGroupsService;
import com.dzics.business.service.ToolCompensationDataService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzToolCompensationData;
import com.dzics.common.model.entity.DzToolGroups;
import com.dzics.common.model.request.toolinfo.GetToolInfoDataListVo;
import com.dzics.common.model.response.GetToolInfoDataListDo;
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
import java.util.ArrayList;
import java.util.List;

/**
 * 刀具信息导出
 *
 * @author ZhangChengJun
 * Date 2021/7/6.
 * @since
 */
@Api(tags = {"Excel刀具信息"})
@RestController
@RequestMapping(value = "/exp/tool/information", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class ToolInformationExp {

    @Autowired
    private BusinessToolGroupsService businessToolGroupsService;
    @Autowired
    private ToolCompensationDataService toolCompensationDataService;

    @ApiOperation(value = "刀具管理", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/tool/management", method = RequestMethod.GET)
    public void getToolGroupsList(HttpServletResponse response,
                                  @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  PageLimit pageLimit,
                                  @RequestParam(value = "groupNo", required = false) String groupNo) throws IOException {


        String fileNameBase = "刀具管理报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            pageLimit.setPage(-1);
            Result<List<DzToolGroups>> listResult = businessToolGroupsService.getToolGroupsList(sub, pageLimit, groupNo);
            List<DzToolGroups> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), DzToolGroups.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "刀具配置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/getToolConfigureList")
    public void getToolConfigureList(HttpServletResponse response,
                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                     PageLimit pageLimit,
                                     @RequestParam(value = "groupNo", required = false) Integer groupNo) throws IOException {
        String fileNameBase = "刀具配置报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            pageLimit.setPage(-1);
            Result<List<DzToolCompensationData>> listResult = toolCompensationDataService.getToolConfigureList(sub, pageLimit, groupNo);
            List<DzToolCompensationData> dataX = listResult.getData();
//            重置对象
            List<ToolconfigurationExp> exps = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(dataX)){
                for (DzToolCompensationData x : dataX) {
                    ToolconfigurationExp exp = new ToolconfigurationExp();
                    exp.setEquipmentNo(x.getEquipmentNo());
                    exp.setEquipmentId(x.getEquipmentId());
                    exp.setGroupNo(x.getGroupNo());
                    exp.setToolNo(x.getToolNo());
                    exp.setCreateTime(x.getCreateTime());
                    exps.add(exp);
                }
            }
            EasyExcel.write(response.getOutputStream(), ToolconfigurationExp.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(exps);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }



    @ApiOperation(value = "刀具信息数据", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/tool/information/data")
    public void getToolInfoDataList(HttpServletResponse response,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                    PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo
    ) throws IOException {


        String fileNameBase = "刀具信息数据报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            pageLimit.setPage(-1);
            Result<List<GetToolInfoDataListDo>> listResult = businessToolGroupsService.getToolInfoDataList(sub, pageLimit, getToolInfoDataListVo);
            List<GetToolInfoDataListDo> dataX = listResult.getData();
            EasyExcel.write(response.getOutputStream(), GetToolInfoDataListDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(dataX);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }
}
