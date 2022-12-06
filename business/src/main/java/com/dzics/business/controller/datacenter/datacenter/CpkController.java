package com.dzics.business.controller.datacenter.datacenter;

import com.dzics.business.service.CpkService;
import com.dzics.common.model.request.DetectorDataQueryCpk;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * cpk数据分析接口
 *
 * @author ZhangChengJun
 * Date 2021/6/28.
 * @since
 */
@Api(tags = {"数据中心"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/charts/data/center")
public class CpkController {

    @Autowired
    private CpkService cpkService;



}
