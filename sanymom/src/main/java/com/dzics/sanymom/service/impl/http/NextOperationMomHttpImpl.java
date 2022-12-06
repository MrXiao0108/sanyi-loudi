package com.dzics.sanymom.service.impl.http;

import com.dzics.common.model.agv.search.MomResultSearch;
import com.dzics.common.model.agv.search.SearchNo;
import com.dzics.common.model.mom.response.RequestHeaderVo;
import com.dzics.common.model.mom.response.ResultVo;
import com.dzics.sanymom.framework.OperLogCallMom;
import com.dzics.sanymom.model.request.agv.AgvParmsDto;
import com.dzics.sanymom.service.HttpService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * 查询下个工序接口
 *
 * @author ZhangChengJun
 * Date 2022/1/13.
 * @since
 */
@Service
@Slf4j
public class NextOperationMomHttpImpl implements HttpService<MomResultSearch, RequestHeaderVo<SearchNo>> {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 发送请求
     * 请求空料框
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
    @OperLogCallMom(operModul = "查询下个工序",operDesc = "查询下个工序")
    @Override
    public MomResultSearch sendPost(String innerGroupId, String orderCode, String lineNo, String groupId, String url, RequestHeaderVo<SearchNo> headerVo, Class<MomResultSearch> responseType) {
        Gson gson = new Gson();
        String reqJson = gson.toJson(headerVo);
        ResponseEntity<MomResultSearch> forEntity = restTemplate.postForEntity(url, reqJson, responseType);
        MomResultSearch body = forEntity.getBody();
        return body;
    }
}
