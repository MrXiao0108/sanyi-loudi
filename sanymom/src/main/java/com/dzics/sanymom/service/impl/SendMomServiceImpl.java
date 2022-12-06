package com.dzics.sanymom.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.dao.DzProductDetectionTemplateMapper;
import com.dzics.common.model.constant.mom.MomReqContent;
import com.dzics.common.model.constant.mom.MomTaskType;
import com.dzics.common.model.constant.mom.MomVersion;
import com.dzics.common.model.entity.MomOrderCompleted;
import com.dzics.common.model.entity.MomUser;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomOrderService;
import com.dzics.sanymom.config.MomRequestPath;
import com.dzics.sanymom.framework.OperLogReportCheck;
import com.dzics.sanymom.model.request.uploadparam.CharList;
import com.dzics.sanymom.model.request.uploadparam.Reported;
import com.dzics.sanymom.service.MomUserMessage;
import com.dzics.sanymom.service.SendMomService;
import com.dzics.sanymom.service.impl.http.SendCheckDataHttpImpl;
import com.dzics.sanymom.util.RedisUniqueID;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SendMomServiceImpl implements SendMomService {
    @Autowired
    MomOrderService momOrderService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private RedisUniqueID redisUniqueID;
    @Autowired
    private MomUserMessage momUserService;
    @Autowired
    private SendCheckDataHttpImpl sendCheckDataHttp;
    @Autowired
    private DzProductDetectionTemplateMapper detectionTemplateMapper;
    @Autowired
    private MomRequestPath momRequestPath;

    @OperLogReportCheck(operModul = "检测记录上传",operDesc = "检测记录上传")
    @Override
    public boolean uploadCheckData(MomOrderCompleted staCode) {
        Map<String, Object> dzWorkpieceData = staCode.getMap();
        Result<MomUser> useLineIslogin = momUserService.getUseLineIslogin();
        MomUser user = useLineIslogin.getData();
        if (user == null) {
            log.warn("质量参数上传到MOM,用户未登录:{}", useLineIslogin);
        }
        String producBarcode = (String) dzWorkpieceData.get("producBarcode");
        if (StringUtils.isEmpty(producBarcode)) {
            log.error("质量参数上传到MOM失败,检测记录未绑定二维码:{}", dzWorkpieceData);
            throw new RuntimeException("质量参数上传到MOM失败,检测记录未绑定二维码");
        }
        String orderNo = String.valueOf(dzWorkpieceData.get("orderNo"));
        String lineNo = String.valueOf(dzWorkpieceData.get("lineNo"));
        RequestHeaderVo<Reported> headerVo = new RequestHeaderVo<>();
        headerVo.setTaskType(MomTaskType.CHECK_UPLOAD);
        headerVo.setVersion(MomVersion.VERSION);
        headerVo.setTaskId(redisUniqueID.getUUID());//随机生成32位UUID，单次下发指令唯一标识
        Reported reported = new Reported();
        reported.setReqSys(MomReqContent.REQ_SYS);
        reported.setFacility(MomReqContent.FACILITY);
        reported.setWipOrderNo(staCode.getWipOrderNo());
        reported.setOprSequenceNo("");
        reported.setSequenceNo("");
        reported.setSerialNo(producBarcode);
        reported.setDeviceID("");
        reported.setWorkStation(staCode.getDzStationCode());
        reported.setResult(Integer.parseInt(dzWorkpieceData.get("outOk").toString()) == 1 ? "OK" : "NG");
        reported.setParamRsrv1("");
        reported.setParamRsrv2("");
        reported.setParamRsrv3("");
        reported.setParamRsrv4(user != null ? user.getEmployeeNo() : "");
        reported.setParamRsrv5("");
        List<Map<String, Object>> templates = detectionTemplateMapper.listMapUpload(String.valueOf(dzWorkpieceData.get("productNo")), orderNo, lineNo);
        if (CollectionUtils.isEmpty(templates)) {
            templates = detectionTemplateMapper.getDefoutDetectionTempUpLoad();
        }
        List<CharList> listList = new ArrayList<>();
        for (Map<String, Object> template : templates) {
            if ("0".equals(String.valueOf(template.get("okShow")))) {
                String colName = String.valueOf(template.get("colName"));
                String colData = String.valueOf(template.get("colData"));
                BigDecimal bigDecimal = new BigDecimal(String.valueOf(dzWorkpieceData.get(colData)));//检测值
                BigDecimal standardValue = new BigDecimal(String.valueOf(template.get("standardValue")));//标准值
                BigDecimal upperValue = new BigDecimal(String.valueOf(template.get("upperValue")));//上限值
                BigDecimal lowerValue = new BigDecimal(String.valueOf(template.get("lowerValue")));//下限值
                CharList charList = new CharList();
                charList.setCharacteristic(colData);
                charList.setCharacteristicName(colName);
                charList.setValue(bigDecimal);
                charList.setTargetValue(standardValue);
                charList.setUpperLimit(upperValue);
                charList.setLowerLimit(lowerValue);
                charList.setAttribute("");
                charList.setParamRsrv1("");
                charList.setParamRsrv2("");
                listList.add(charList);
            }
        }
        //填充实测参数数值列表
        reported.setCharList(listList);
        headerVo.setReported(reported);
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        log.info("上发检测数据到MOM:{}", reqJson);
        ResultVo resultVo = sendCheckDataHttp.sendPost(redisUniqueID.getGroupId(), orderNo, lineNo, redisUniqueID.getGroupId(), momRequestPath.ipPortPath, headerVo, ResultVo.class);
        String code = resultVo.getCode();
        if (MomReqContent.MOM_CODE_OK.equals(code)) {
//               请求正常
            return true;
        } else {
            log.error("质量参数上传到MOM失败,返回结果:{}", JSONObject.toJSONString(resultVo));
            throw new RuntimeException("质量参数上传到MOM失败");
        }
    }

}
