package com.dzics.sanymom.controller;

import com.alibaba.fastjson.JSONObject;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.sanymom.exception.CustomMomException;
import com.dzics.sanymom.model.ResultDto;
import com.dzics.sanymom.model.request.agv.AutomaticGuidedVehicle;
import com.dzics.sanymom.model.request.sany.IssueOrderInformation;
import com.dzics.sanymom.model.request.syncuser.SyncMomUser;
import com.dzics.sanymom.service.AgvRobackService;
import com.dzics.sanymom.service.SyncMomUserService;
import com.dzics.sanymom.service.SaveMomOrderService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * 下发订单
 *
 * @author ZhangChengJun
 * Date 2021/5/28.
 * @since
 */
@Api(tags = {"总控订单"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@Controller
@Slf4j
public class PlacingOrdersController {
    @Autowired
    private SaveMomOrderService saveMomOrderService;
    @Autowired
    private AgvRobackService agvRobackService;
    @Autowired
    private SyncMomUserService syncMomUserService;
    /**
     * 端口8082
     * 总控->中控 订单任务下发
     *
     * @param momParms
     * @return
     */
    @ApiOperation(value = "接收MOM请求统一API")
    @PostMapping("/SANY/gateway/pdev/sany/slaveId/task/post")
    public ResultDto myRequest(@RequestBody String momParms) {
        log.info("收到到MOM请求参数：{}", momParms);
        JSONObject jsonObject = JSONObject.parseObject(momParms);
        String taskType = String.valueOf(jsonObject.get("taskType"));
        Gson gson = new Gson();
        if (MomTaskType.MOM_ORDER_TYPE.equals(taskType)) {
//              生产订单下发
            IssueOrderInformation requestHeaderVo = gson.fromJson(momParms, IssueOrderInformation.class);
            ResultDto resultDto = saveMomOrderService.saveMomOrderService(requestHeaderVo,momParms);
            return resultDto;
        } else if (MomTaskType.AGV_HANDLING_FEEDBACK.equals(taskType)) {
//                AGV搬运反馈信息确认到中控
            AutomaticGuidedVehicle automaticGuidedVehicle = gson.fromJson(momParms, AutomaticGuidedVehicle.class);
            ResultDto resultDto = agvRobackService.automaticGuidedVehicle(automaticGuidedVehicle);
            return resultDto;
        } else if (MomTaskType.SYN_USER_MESSAGE.equals(taskType)) {
            SyncMomUser syncMomUser = gson.fromJson(momParms, SyncMomUser.class);
            ResultDto resultDto = syncMomUserService.syncUser(syncMomUser);
            return resultDto;
        } else {
            String taskID = String.valueOf(jsonObject.get("taskId"));
            log.error("接口 taskID:{} 类型 taskType:{} 类型未识别", taskID, taskType);
            throw new CustomMomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR74.getChinese(), MomVersion.VERSION, taskID);
        }
    }




}
