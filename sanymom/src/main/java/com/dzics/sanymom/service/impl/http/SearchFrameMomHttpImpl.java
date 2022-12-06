package com.dzics.sanymom.service.impl.http;

import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.sanymom.framework.OperLogCallMom;
import com.dzics.sanymom.model.base.MomResult;
import com.dzics.sanymom.model.request.searchframe.MaterialFrame;
import com.dzics.sanymom.service.HttpService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 查询料框参数接口
 *
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
@Service
@Slf4j
public class SearchFrameMomHttpImpl implements HttpService<MomResult, RequestHeaderVo<MaterialFrame>> {
    @Autowired
    private RestTemplate restTemplate;
    /**
     * 发送请求
     * 查询料框参数接口
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
    @OperLogCallMom(operModul = "请求MOM查询",operDesc = "查询料框接口")
    @Override
    public MomResult sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url,  RequestHeaderVo<MaterialFrame> headerVo, Class<MomResult> responseType) {
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        try {
            ResponseEntity<MomResult> forEntity = restTemplate.postForEntity(url, reqJson, responseType);
            return forEntity.getBody();
        }catch (Throwable throwable){
            log.error("查询料框接口异常：{}",throwable.getMessage());
            ResponseEntity<Object> objectResponseEntity = restTemplate.postForEntity(url, reqJson, Object.class);
            log.info("xxx:{}",objectResponseEntity.getBody());
            return null;
        }
    }
}
