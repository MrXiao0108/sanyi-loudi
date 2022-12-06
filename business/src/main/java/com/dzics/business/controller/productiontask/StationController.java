package com.dzics.business.controller.productiontask;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.productiontask.station.AddWorkStation;
import com.dzics.business.model.vo.productiontask.station.SelWorkStation;
import com.dzics.business.model.vo.productiontask.station.UpdateWorkStation;
import com.dzics.business.service.BusStationManageService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.PutProcessShowVo;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 工位管理
 *
 * @author ZhangChengJun
 * Date 2021/5/18.
 * @since
 */
@Api(tags = {"工位管理"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/station")
@Controller
public class StationController {
    @Autowired
    private BusStationManageService busStationManageService;

    @OperLog(operModul = "工位管理", operType = OperType.ADD, operDesc = "新增工位", operatorType = "后台")
    @ApiOperation(value = "新增", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PostMapping
    public Result addWorkingStation(
            @Valid @RequestBody AddWorkStation workStation,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busStationManageService.addWorkingStation(workStation, sub);
    }


    @OperLog(operModul = "工位管理", operType = OperType.UPDATE, operDesc = "编辑", operatorType = "后台")
    @ApiOperation(value = "编辑", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PutMapping
    public Result updateWorkingStation(
            @Valid @RequestBody UpdateWorkStation station,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busStationManageService.updateWorkingStation(station, sub);
    }


    @OperLog(operModul = "工位管理", operType = OperType.UPDATE, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @DeleteMapping("/{stationId}")
    public Result delWorkingStation(
            @PathVariable("stationId") @ApiParam(name = "工位ID", required = true) String stationId,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busStationManageService.delWorkingStation(stationId, sub);
    }


    @ApiOperation(value = "工位列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @GetMapping
    public Result getWorkingStation(SelWorkStation selWorkStation,
                                    @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result workingStation = busStationManageService.getWorkingStation(selWorkStation, sub);
        return workingStation;
    }

    @OperLog(operModul = "工位管理", operType = OperType.UPDATE, operDesc = "编辑是否展示", operatorType = "后台")
    @ApiOperation(value = "编辑看板是否展示", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "xnb", order = 113)
    @PutMapping("/putShowById")
    public Result putOnOff(
            @Valid @RequestBody PutProcessShowVo pShow,
            @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busStationManageService.putOnoff(pShow);
    }
}
