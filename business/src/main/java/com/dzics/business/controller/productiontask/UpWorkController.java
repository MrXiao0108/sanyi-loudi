package com.dzics.business.controller.productiontask;

import com.dzics.business.service.MomDzWorkingFlowService;
import com.dzics.common.model.entity.DzWorkStationManagement;
import com.dzics.common.model.request.mom.GetWorkingDetailsVo;
import com.dzics.common.model.request.mom.LineIdWorkStation;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.GetWorkingDetailsDo;
import com.dzics.common.service.DzWorkStationManagementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 生产任务报工
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Api(tags = {"生产任务报工"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/upWork")
@Controller
public class UpWorkController {

    @Autowired
    private MomDzWorkingFlowService momDzWorkingFlowService;
    @Autowired
    private DzWorkStationManagementService dzWorkStationManagementService;

    @ApiOperation(value = "生产任务报工详情列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping
    public Result<GetWorkingDetailsDo> getWorkingDetails(GetWorkingDetailsVo getWorkingDetailsVo,
                                                         @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return momDzWorkingFlowService.getWorkingDetails(getWorkingDetailsVo);
    }

    @ApiOperation(value = "根据产线ID查询工位", consumes = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping("/station")
    public Result<DzWorkStationManagement> getLineWorkStation(@Valid LineIdWorkStation workStation,
                                                              @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzWorkStationManagementService.getLineId(workStation);
    }
}
