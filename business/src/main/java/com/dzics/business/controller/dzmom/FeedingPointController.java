package com.dzics.business.controller.dzmom;

import com.dzics.business.service.BusMomMaterialPointService;
import com.dzics.common.model.request.mom.AddFeedingVo;
import com.dzics.common.model.request.mom.GetFeedingAgvVo;
import com.dzics.common.model.request.mom.UpdateFeedingVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.mom.DzicsStationCode;
import com.dzics.common.model.response.mom.GetFeedingAgvDo;
import com.dzics.common.service.MomMaterialPointService;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author xnb
 * @date 2021年11月04日 13:30
 */
@Api(tags = {"AGV投料点管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/mom/agv")
@RestController
public class FeedingPointController {
    @Autowired
    private MomMaterialPointService momMaterialPointService;
    @Autowired
    private BusMomMaterialPointService pointService;

    @ApiOperation(value = "查询", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 1)
    @GetMapping
    public Result<GetFeedingAgvDo> getFeedingAGV(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                 @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                 GetFeedingAgvVo getFeedingAgvVo) {
        return momMaterialPointService.getFeedingPoints(getFeedingAgvVo);
    }


    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 2)
    @PostMapping
    public Result saveFeeding(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                              @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                              @Valid @RequestBody AddFeedingVo addFeedingVo) {
        Result result = pointService.addFeedingPoint(sub, addFeedingVo);
        return result;

    }

    @ApiOperation(value = "修改", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 3)
    @PutMapping
    public Result putFeeding(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                             @Valid @RequestBody UpdateFeedingVo addFeedingVo
    ) {
        return pointService.putFeedingPoint(sub, addFeedingVo);
    }

    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 4)
    @DeleteMapping("/{materialPointId}")
    public Result delFeeding(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                             @PathVariable("materialPointId") String materialPointId) {
        return momMaterialPointService.delFeedingPoint(materialPointId);
    }


    @ApiOperation(value = "查询DZICS工位", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 1)
    @GetMapping("/line/station")
    public Result<DzicsStationCode> getDzicsStationCode(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                                        @RequestParam(value = "lineId",required = true) @ApiParam(value = "产线ID",required = true) String lineId) {
        return momMaterialPointService.getDzicsStationCode(lineId);
    }
}
