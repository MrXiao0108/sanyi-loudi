package com.dzics.sanymom.service.impl.http;

import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultSearchOrderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.sanymom.framework.OperLogCallMom;
import com.dzics.sanymom.model.request.uploadparam.Reported;
import com.dzics.sanymom.service.HttpService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Classname SendCheckDataHttpImpl
 * @Description 描述
 * @Date 2022/5/20 14:46
 * @Created by NeverEnd
 */
@Service
@Slf4j
public class SendCheckDataHttpImpl implements HttpService<ResultVo, RequestHeaderVo<Reported>> {
    @Autowired
    private RestTemplate restTemplate;

    @OperLogCallMom(operModul = "检测", operDesc = "发送检测数据")
    @Override
    public ResultVo sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url,  RequestHeaderVo<Reported> headerVo, Class<ResultVo> responseType) {
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        ResponseEntity<ResultVo> forEntity = restTemplate.postForEntity(url, reqJson, responseType);
        ResultVo body = forEntity.getBody();
        if (body!=null){
            body.setStatusCode(forEntity.getStatusCode().value());
        }
        return body;
    }
}
