package com.dzics.business.controller.product;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.detectordata.AddDetectorPro;
import com.dzics.business.model.vo.detectordata.GroupId;
import com.dzics.business.model.vo.detectordata.ProDuctCheck;
import com.dzics.business.model.vo.detectordata.check.DetectionTemplateParm;
import com.dzics.business.model.vo.detectordata.edit.EditProDuctTemp;
import com.dzics.business.service.BusDetectorDataService;
import com.dzics.common.enums.OperType;
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
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 检测设置
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Api(tags = {"产品检测配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/detection/item")
@Controller
public class DetectionSetController {
    @Autowired
    private BusDetectorDataService busDetectorDataService;

    /**
     * 检测记录
     */
    @ApiOperation(value = "检测设置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping(value = "/all")
    public Result<DetectionTemplateParm> selDetectorItem(GroupId groupId, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.selDetectorItem(groupId, sub);
    }

    @OperLog(operModul = "产品检测配置", operType = OperType.ADD, operDesc = "新增检测", operatorType = "后台")
    @ApiOperation(value = "新增检测", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PostMapping
    public Result addDetectorItem(@RequestBody @Valid AddDetectorPro detectorPro, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.addDetectorItem(detectorPro, sub);
    }

    @ApiOperation(value = "配置列表", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @GetMapping
    public Result queryProDetectorItem(ProDuctCheck proDuctCheck, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                       @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        Result listResult = busDetectorDataService.queryProDetectorItem(proDuctCheck, sub);
        return listResult;
    }




    @OperLog(operModul = "产品检测配置", operType = OperType.UPDATE, operDesc = "修改检测配置", operatorType = "后台")
    @ApiOperation(value = "修改检测配置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping
    public Result editProDetectorItem(@RequestBody @Valid EditProDuctTemp editProDuctTemp, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                      @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.editProDetectorItem(editProDuctTemp, sub);
    }


    @OperLog(operModul = "产品检测配置", operType = OperType.UPDATE, operDesc = "修改检测配置", operatorType = "后台")
    @ApiOperation(value = "对比值修改", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @PutMapping("/contrast")
    public Result dbProDetectorItem(@RequestBody @Valid EditProDuctTemp editProDuctTemp, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.dbProDetectorItem(editProDuctTemp, sub);
    }
}
