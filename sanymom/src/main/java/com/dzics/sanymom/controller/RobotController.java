package com.dzics.sanymom.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.dao.MomOrderMapper;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.agv.search.SearchDzdcMomSeqenceNo;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MonOrder;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.service.ProTaskOrderService;
import com.dzics.sanymom.service.impl.CallAgvBoxServiceImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * 下发订单
 *
 * @author ZhangChengJun
 * Date 2021/5/28.
 * @since
 */
@Api(tags = {"机器人请求"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Controller
@Slf4j
public class RobotController {
    @Autowired
    private ProTaskOrderService proTaskOrderService;
    @Autowired
    private CallAgvBoxServiceImpl callAgvService;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MomOrderMapper momOrderService;
    @Autowired
    private CachingApi cachingApi;

    /**
     * 工序间配送，拉料 和 送空料框
     *
     * @param dzdc
     * @return
     */
    @ApiOperation(value = "机器人请求AGV", notes = "返回 data中值 OKOK 请求AGV成功", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/call/material")
    public Result chlickOkMaterial(@RequestBody EmptyFrameMovesDzdc dzdc) {
        dzdc.setGroupId(redisUniqueID.getGroupId());
        dzdc.setInnerGroupId(redisUniqueID.getGroupId());
        dzdc.setDeviceType("机器人");
        Result result = callAgvService.callAgv(dzdc);
        return result;
    }

    @ApiOperation(value = "查询下个工序", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperationSupport(author = "NeverEnd", order = 8)
    @PostMapping("/get/next/seqenceno")
    public Result searechOprSequenceNo(@RequestBody SearchDzdcMomSeqenceNo momSeqenceNo) {
        momSeqenceNo.setGroupId(redisUniqueID.getGroupId());
        Result result = proTaskOrderService.searechOprSequenceNo(momSeqenceNo);
        log.info("机器人请求AGV 响应信息: {}", JSONObject.toJSONString(result));
        return result;
    }

    /**
     * 服务启动 检查是否存在缓存信息
     * */
    @PostConstruct
    public void vialdRedisRob() {
        try {
            DzProductionLine line = cachingApi.getOrderIdAndLineId();

            //检查当前产品型号
            if (redisUtil.get(RedisKey.Now_Work_ProductAlias + line.getOrderNo() + line.getLineNo())==null){
                MonOrder monOrder = momOrderService.selectOne(new QueryWrapper<MonOrder>().eq("order_id", line.getOrderId()).eq("WipOrderNo", "DZICS-Manual"));
                redisUtil.set(RedisKey.Now_Work_ProductAlias+line.getOrderNo()+line.getLineNo(),monOrder.getProductAlias());
            }

            //初始化 服务启动报工状态缓存
            if(redisUtil.get(RedisKey.Work_Report_Status+line.getOrderNo()+line.getLineNo()) == null){
                redisUtil.set(RedisKey.Work_Report_Status+line.getOrderNo()+line.getLineNo(),"1");
            }

            //初始化 服务启动当前人工打磨台2最新的检测产品二维码
            if(redisUtil.get(RedisKey.Get_Polish_QrCode+line.getOrderNo())==null){
                redisUtil.set(RedisKey.Get_Polish_QrCode+line.getOrderNo(),"");
            }
        }catch(Throwable throwable){
            throwable.printStackTrace();
        }
    }

}
