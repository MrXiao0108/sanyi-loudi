package com.dzics.business.controller.datacenter.datacenter;

import com.dzics.business.model.vo.detectordata.ProcessingBeat;
import com.dzics.business.model.vo.detectordata.ResProcessingBeat;
import com.dzics.business.service.BusinessProductionPlanService;
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
 * 加工节拍
 *
 * @author ZhangChengJun
 * Date 2021/6/2.
 * @since
 */
@Api(tags = {"数据中心"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/charts/data/center")
public class ProcessingBeatController {
    @Autowired
    private BusinessProductionPlanService businessProductionPlanService;

    @ApiOperation(value = "加工节拍", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 2)
    @GetMapping(value = "/processing/beat")
    public Result<ResProcessingBeat> getProcessingBeat(@Valid ProcessingBeat processingBeat,
                                                       @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return businessProductionPlanService.getProcessingBeat(sub, processingBeat);
    }
}
