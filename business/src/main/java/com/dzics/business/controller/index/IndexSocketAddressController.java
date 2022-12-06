package com.dzics.business.controller.index;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.GetSocketAddressPram;
import com.dzics.business.service.PageHomeService;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.request.SelectTrendChartVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.SelectTrendChartDo;
import com.dzics.common.model.response.homepage.GetDayAndMonthDataDo;
import com.dzics.common.model.response.homepage.QualifiedAndOutputDo;
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
 * 首页socket接口地址获取
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 * @since
 */
@Api(tags = {"首页接口"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/index")
@Controller
public class IndexSocketAddressController {
   @Autowired
   private DzDetectionTemplCache dzDetectionTemplCache;
   @Autowired
   PageHomeService pageHomeService;

   @ApiOperation(value = "首页查询产出率和合格率")
   @ApiOperationSupport(author = "jq", order = 1)
   @GetMapping("/getOutputAndQualified/{lineId}")
   public Result<QualifiedAndOutputDo> getOutputAndQualified(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                             @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                             @PathVariable("lineId")Long lineId) {
      Result result = pageHomeService.getOutputAndQualified(lineId);
      return result;
   }

   @ApiOperation(value = "首页查询产线日产和月产")
   @ApiOperationSupport(author = "jq", order = 1)
   @GetMapping("/geDayAndMonthData/{lineId}")
   public Result<GetDayAndMonthDataDo> geDayAndMonthData(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                         @PathVariable("lineId")Long lineId) {
      Result result = pageHomeService.geDayAndMonthDataV2(lineId);
      return result;
   }

   @ApiOperation(value = "首页查询设备信息")
   @ApiOperationSupport(author = "jq", order = 1)
   @GetMapping("/geEquipmentState/{lineId}")
   public Result geEquipmentState(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌") String tokenHdaer,
                                                         @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号") String sub,
                                                         @PathVariable("lineId")Long lineId) {
      Result result = pageHomeService.geEquipmentState(lineId);
      return result;
   }
}
