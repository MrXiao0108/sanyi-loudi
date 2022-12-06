package com.dzics.business.controller.product;

import com.dzics.business.common.annotation.OperLog;
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

/**
 * 检测设置
 *
 * @author ZhangChengJun
 * Date 2021/2/5.
 * @since
 */
@Api(tags = {"产品检测配置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/detection/itemdel")
@Controller
public class DetectionDelController {
    @Autowired
    private BusDetectorDataService busDetectorDataService;

    @OperLog(operModul = "产品检测配置", operType = OperType.DEL, operDesc = "删除检测配置", operatorType = "后台")
    @ApiOperation(value = "删除检测配置", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 111)
    @DeleteMapping("/{groupId}")
    public Result delProDetectorItem(@PathVariable("groupId") Long groupId, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return busDetectorDataService.delProDetectorItem(groupId, sub);
    }
}
