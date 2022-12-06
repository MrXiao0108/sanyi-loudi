package com.dzics.sanymom.controller;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.model.entity.DzWorkpieceData;
import com.dzics.common.model.response.Result;
import com.dzics.sanymom.service.impl.mq.MomSendCheckDataLocalImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname CheckProductController
 * @Description 描述
 * @Date 2022/5/13 16:32
 * @Created by NeverEnd
 */
@Api(tags = {"接收数据"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping("/api/receive/data")
@RestController
@Slf4j
public class CheckProductController {
    @Value("${mom.upload.quality.param.queue}")
    private String queryName;
    @Value("${mom.upload.quality.param.exchange}")
    private String exchange;
    @Value("${mom.upload.quality.param.routing}")
    private String routing;
    @Autowired
    private MomSendCheckDataLocalImpl checkDataLocal;


    @ApiOperation(value = "接收检测数据")
    @PostMapping("/detected/data")
    public Result dzWorkpieceData(@RequestBody DzWorkpieceData momParms) {
        log.info("订单：{},接收检测数据 此接口已废弃: {}", momParms.getOrderNo(), JSONObject.toJSONString(momParms));
//        boolean b = checkDataLocal.sendMq(momParms, routing, exchange, queryName);
        return Result.OK(true);
    }

}
