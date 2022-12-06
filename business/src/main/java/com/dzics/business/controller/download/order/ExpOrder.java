package com.dzics.business.controller.download.order;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.service.BusinessOrderService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.request.order.OrderParmsModel;
import com.dzics.common.model.response.DzOrderDo;
import com.dzics.common.model.response.Result;
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
 * 导出订单
 *
 * @author ZhangChengJun
 * Date 2021/7/8.
 * @since
 */
@Api(tags = {"Excel订单管理"})
@RestController
@RequestMapping(value = "/exp/order", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class ExpOrder {

    @Autowired
    private BusinessOrderService orderService;


    @ApiOperation(value = "订单管理", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.GET)
    public void list(HttpServletResponse response,
                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                     OrderParmsModel orderParmsModel) throws IOException {


        String fileNameBase = "订单管理数据报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            orderParmsModel.setPage(-1);
            Result<List<DzOrderDo>> listResult = orderService.list(sub, orderParmsModel);
            List<DzOrderDo> data = listResult.getData();
            EasyExcel.write(response.getOutputStream(), DzOrderDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }

}
