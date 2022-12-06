package com.dzics.business.controller.dzmom.rob;

import com.alibaba.fastjson.JSONObject;
import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.service.agv.AgvService;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.request.dzcheck.DzOrderCheck;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = {"机器人-检验订单"}, produces = "ROB检验订单相关")
@RequestMapping("/api/agv")
@RestController
@Slf4j
public class DzMomCheckController {
    @Autowired
    private AgvService agvService;

    @OperLog(operModul = "ROB检验订单", operType = OperType.QUERY, operDesc = "校验当前物料和在做订单", operatorType = "后台")
    @ApiOperation(value = "校验物料", notes = "返回 data中值 OKOK 为校验通过，其他不通过 ", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 112)
    @PostMapping(value = "/order/check")
    public Result checkOrder(@RequestBody DzOrderCheck dzOrderCheck) {
        String orderCode = dzOrderCheck.getOrderCode();
        String lineNo = dzOrderCheck.getLineNo();
        log.info("机器人扫码后校验物料 订单：{}, 产线：{}, 信息 ：{}", orderCode, lineNo, JSONObject.toJSONString(dzOrderCheck));
        try {
            Result res = agvService.checkOrder(dzOrderCheck);
            log.error("执行真实校验物料结果：{}", JSONObject.toJSONString(res));
        } catch (Throwable throwable) {
            log.error("执行真实校验物料错误：{}", throwable.getMessage(), throwable);
        }
        Result<Object> result = Result.ok();
        result.setData("OKOK");
        log.info("机器人扫码后校验物料 订单：{}, 产线：{},  返回结果：{}", orderCode, lineNo, JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 工序间配送，拉料 和 送空料框
     *
     * @param emptyFrameMovesDzdc
     * @return
     */
    @OperLog(operModul = "机器人AGV", operType = OperType.ADD, operDesc = "机器人请求叫料", operatorType = "后台")
    @ApiOperation(value = "机器人请求叫料",notes = "返回 data中值 OKOK 请求叫料成功",consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/call/material")
    public Result chlickOkMaterial(@RequestBody EmptyFrameMovesDzdc emptyFrameMovesDzdc) {
        return agvService.processDistribution(emptyFrameMovesDzdc);
    }
}
