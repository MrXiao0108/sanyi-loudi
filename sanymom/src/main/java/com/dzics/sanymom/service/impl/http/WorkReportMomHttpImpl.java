package com.dzics.sanymom.service.impl.http;

import com.dzics.common.model.mom.response.GeneralControlModel;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.sanymom.framework.OperLogCallMom;
import com.dzics.sanymom.service.HttpService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
@Slf4j
@Service
public class WorkReportMomHttpImpl implements HttpService<ResultVo,RequestHeaderVo<List<GeneralControlModel>> > {
    @Autowired
    private RestTemplate restTemplate;
    /**
     * 发送请求
     *
     *
     * @param innerGroupId
     * @param orderCode
     * @param lineNo
     * @param groupId
     * @param url
     * @param headerVo
     * @param responseType
     * @return
     */
    @OperLogCallMom(operModul = "报工",operDesc = "请求MOM报工")
    @Override
    public ResultVo sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url, RequestHeaderVo<List<GeneralControlModel>> headerVo, Class<ResultVo> responseType) {
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
//        log.info("TEST请求MOM报工，开始请求参数：{}",reqJson);
        ResponseEntity<ResultVo> forEntity = restTemplate.postForEntity(url, reqJson, responseType);
//        log.info("TEST请求MOM报工，结束请求参数：{}",reqJson);
        ResultVo body = forEntity.getBody();
        if (body!=null){
            body.setStatusCode(forEntity.getStatusCode().value());
        }
        return body;
    }
}
