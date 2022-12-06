package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.agv.EmptyFrameMovesDzdc;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.entity.LogPromptMsg;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.impl.LogPromptMsgServiceImpl;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.service.impl.mq.MomSendReportLocalImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.dzics.sanymom.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @Classname MomLogPromptMsgImpl
 * @Description 描述
 * @Date 2022/5/5 16:55
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class MomLogPromptMsgImpl extends LogPromptMsgServiceImpl {
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private MomSendReportLocalImpl momSendReportLocal;
    @Value("${mom.accq.product.position.query}")
    private String queryName;
    @Autowired
    private CallAgvBoxServiceImpl callAgvService;
    @Value("${mom.accq.product.position.routing}")
    private String routing;
    @Value("${mom.accq.product.position.exchange}")
    private String exchange;

    public Result cancelRequest(String logId) {
        LogPromptMsg promptMsg = new LogPromptMsg();
        promptMsg.setLogId(logId);
        promptMsg.setHandle(1);
        promptMsg.setDelFlag(true);
        updateById(promptMsg);
        return Result.ok();
    }


    public Result againRequest(String logId) {
        String key = RedisKey.AGAIN_REQUEST_LOGID + logId;
        Object agina = redisUtil.get(key);
        if (agina != null) {
            long expire = redisUtil.getExpire(key);
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR, "请在" + expire + "秒后重试");
        }
        redisUtil.set(key, 1, 62);
        LogPromptMsg byId = getById(logId);
        if (byId == null) {
            throw new CustomException(CustomExceptionType.OK_NO_DATA);
        }
        String invokParm = byId.getInvokParm();
        String reqType = byId.getReqType();
        if ("报工".equals(reqType)) {
            ReqWorkQrCodeOrder emptyFrameMovesDzdc = JSONObject.parseObject(invokParm, ReqWorkQrCodeOrder.class);
            boolean b = momSendReportLocal.reportWorkMq(emptyFrameMovesDzdc,routing,exchange,queryName);
            return Result.ok(b);
        } else {
            EmptyFrameMovesDzdc emptyFrameMovesDzdc = JSONObject.parseObject(invokParm, EmptyFrameMovesDzdc.class);
            emptyFrameMovesDzdc.setInnerGroupId(redisUniqueID.getGroupId());
            if (StringUtils.isEmpty(emptyFrameMovesDzdc.getGroupId())) {
                emptyFrameMovesDzdc.setGroupId(redisUniqueID.getGroupId());
            }
            Result result = callAgvService.callAgv(emptyFrameMovesDzdc);
            return result;
        }
    }
}
