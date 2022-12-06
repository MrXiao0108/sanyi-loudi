package com.dzics.business.controller.dzmom.agv;

import com.alibaba.fastjson.JSONObject;
import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.DzMomReceiveMaterialService;
import com.dzics.business.service.agv.AgvService;
import com.dzics.business.service.mq.RabbitmqService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.entity.MomReceiveMaterial;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.request.agv.AgvClickSignal;
import com.dzics.common.model.request.agv.AgvClickSignalConfirmV2;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimitAgv;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Api(tags = {"AGV"}, produces = "AGV接口相关")
@RequestMapping("/api/agv/dispatch")
@RestController
@Slf4j
public class AgvController {

    @Autowired
    private AgvService agvService;

    @Autowired
    private RabbitmqService rabbitmqService;
    @Autowired
    private DzMomReceiveMaterialService dzMomReceiveMaterialService;

    @OperLog(operModul = "AGV", operType = OperType.ADD, operDesc = "点击到料信号", operatorType = "后台")
    @ApiOperation(value = "到料信号点击", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/car/click/signal")
    public Result chlickSignal(@Valid @RequestBody AgvClickSignal clickSignal) {
        String lineNo = clickSignal.getLineNo();
        String orderNo = clickSignal.getOrderNo();
        String jsonString = JSONObject.toJSONString(clickSignal);
        log.info("到料信号触发 订单：{} , 产线:{}  ,参数: {}", orderNo, lineNo, jsonString);
        Result result = agvService.chlickSignal(clickSignal);
        log.info("到料信号触发 订单：{} , 产线:{}  ,响应结果: {}", orderNo, lineNo, jsonString);
        return result;
    }


    @ApiOperation(value = "接收sanymom日志信息", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/car/click/log")
    public Result chlickLog(@RequestBody SysRealTimeLogs clickSignal) {
        boolean b = rabbitmqService.sendRabbitmqLog(JSONObject.toJSONString(clickSignal));
        return Result.OK(b);
    }


    @ApiOperation(value = "扫描FRID", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/material/frid")
    public Result getFrid(@Valid @RequestBody AgvClickSignal clickSignal) {
        return agvService.getFrid(clickSignal);
    }

    @OperLog(operModul = "AGV", operType = OperType.ADD, operDesc = "确认来料点击", operatorType = "后台")
    @ApiOperation(value = "确认来料点击", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/car/click/confirm")
    public Result chlickOkMaterial(@Valid @RequestBody AgvClickSignalConfirmV2 confirm) {
        Result result = agvService.chlickOkConfirmMaterialV2(confirm);
        log.error("确认来料触发 完成: 响应信息: {}", JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "历史来料记录", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @GetMapping("/material/history")
    public Result<MomReceiveMaterial> chlickOkMaterialHistory(PageLimitAgv limitAgv) {
        return dzMomReceiveMaterialService.chlickOkMaterialHistory(limitAgv);
    }


}
