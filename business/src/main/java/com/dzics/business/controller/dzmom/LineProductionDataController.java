package com.dzics.business.controller.dzmom;

import com.dzics.business.service.BusinessProductionPlanDayService;
import com.dzics.common.model.request.mom.LineProductionDataVO;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Api(tags = {"产线生产数据管理"}, produces = "产线生产数据管理")
@RequestMapping("/api/line/production/data")
@RestController
public class LineProductionDataController {
    @Autowired
    BusinessProductionPlanDayService businessProductionPlanDayService;

    @ApiOperation(value = "查询多条产线的日生产数据", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "jq", order = 10)
    @PostMapping
    public Result getLineProductionData(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                        @RequestBody LineProductionDataVO lineProductionDataVO
                                        ) {
        return businessProductionPlanDayService.getLineProductionData(lineProductionDataVO);
    }
}
