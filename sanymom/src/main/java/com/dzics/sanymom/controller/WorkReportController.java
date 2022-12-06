package com.dzics.sanymom.controller;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.custom.WorkReportDto;
import com.dzics.common.model.response.Result;
import com.dzics.sanymom.service.impl.mq.MomSendReportLocalImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname WorkReportController
 * @Description 描述
 * @Date 2022/5/13 16:14
 * @Created by NeverEnd
 */
@Api(tags = {"接收数据"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/receive/data")
@RestController
@Slf4j
@Scope("singleton")
public class WorkReportController {

    @Autowired
    private MomSendReportLocalImpl momSendReportLocal;
    @Value("${mom.accq.product.position.query}")
    private String queryName;
    @Value("${mom.accq.product.position.routing}")
    private String routing;
    @Value("${mom.accq.product.position.exchange}")
    private String exchange;

    @Autowired
    private RedisUniqueID redisUniqueID;

    @ApiOperation(value = "接收报工数据")
    @PostMapping("/work/report")
    public Result workReport(@RequestBody WorkReportDto momParms) {
        log.info("订单：{},触发报工: {}", momParms.getOrderNo(), JSONObject.toJSONString(momParms));
        String groupId = redisUniqueID.getGroupId();
        momParms.setGroupId(groupId);
        boolean b = momSendReportLocal.sendMq(momParms, routing, exchange,queryName);
        return Result.OK(b);
    }
}
