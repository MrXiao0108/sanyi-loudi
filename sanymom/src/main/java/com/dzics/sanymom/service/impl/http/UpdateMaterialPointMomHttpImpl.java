package com.dzics.sanymom.service.impl.http;

import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.sanymom.framework.OperLogCallMom;
import com.dzics.sanymom.model.request.PutFeedingPoint;
import com.dzics.sanymom.service.HttpService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 更新料框请求
 *
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
@Service
@Slf4j
public class UpdateMaterialPointMomHttpImpl implements HttpService<ResultVo, RequestHeaderVo<PutFeedingPoint>> {
    @Autowired
    private RestTemplate restTemplate;
    /**
     * 发送请求
     *请求MOM更新料点状态
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
    @OperLogCallMom(operModul = "请求MOM更新",operDesc = "更新料点状态")
    @Override
    public ResultVo sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url,  RequestHeaderVo<PutFeedingPoint> headerVo, Class<ResultVo> responseType) {
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
