package com.dzics.business.controller.download.product;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.business.framework.excel.CustomCellWriteHandler;
import com.dzics.business.model.vo.ProductListModel;
import com.dzics.business.model.vo.detectordata.ProDuctCheck;
import com.dzics.business.service.BusDetectorDataService;
import com.dzics.business.service.BusinessProductService;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.response.DzProductDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.down.ExpDetectorItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品管理导出
 *
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Api(tags = {"Excel产品管理"})
@RestController
@RequestMapping(value = "/exp/product/manage", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
@Slf4j
public class ProductManage {
    @Autowired
    private BusinessProductService businessProductService;
    @Autowired
    private BusDetectorDataService busDetectorDataService;

    @ApiOperation(value = "产品管理", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(method = RequestMethod.GET)
    public void list(HttpServletResponse response,
                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                     ProductListModel productListModel) throws IOException {


        String fileNameBase = "产品管理数据报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            productListModel.setPage(-1);
            Result<List<DzProductDo>> listResult = businessProductService.list(sub, productListModel);
            List<DzProductDo> data = listResult.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                data.forEach(d -> {
                    try {
                        d.setUrl(new URL(d.getPicture()));
                    } catch (MalformedURLException e) {
                        log.error("设置产品缩略图异常：{}", e.getMessage(), e);
                    }
                });
            }
            EasyExcel.write(response.getOutputStream(), DzProductDo.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(data);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }
    }


    @ApiOperation(value = "产品检测", consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/detection", method = RequestMethod.GET)
    public void queryProDetectorItem(HttpServletResponse response, ProDuctCheck proDuctCheck,
                                     @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) throws IOException {

        String fileNameBase = "产品检测数据报表";
        try {
            String fileName = URLEncoder.encode(fileNameBase + "-" + System.currentTimeMillis(), "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE + ";charset=UTF-8");
            Result<List<LinkedHashMap<String, Object>>> listResult = busDetectorDataService.queryProDetectorItem(proDuctCheck, sub);
            List<LinkedHashMap<String, Object>> data = listResult.getData();
            List<ExpDetectorItem> detectorItems = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(data)) {
                for (LinkedHashMap<String, Object> datum : data) {
                    ExpDetectorItem detectorItem = new ExpDetectorItem();
                    BeanUtils.populate(detectorItem,datum);
                    detectorItems.add(detectorItem);
                }
            }
            EasyExcel.write(response.getOutputStream(), ExpDetectorItem.class).registerWriteHandler(new CustomCellWriteHandler()).sheet(fileNameBase).doWrite(detectorItems);
        } catch (Throwable throwable) {
            log.error("导出{}异常：{}", fileNameBase, throwable.getMessage(), throwable);
            response.setCharacterEncoding("utf-8");
            Result error = Result.error(CustomExceptionType.SYSTEM_ERROR);
            response.getWriter().println(JSON.toJSONString(error));
        }

    }


}
