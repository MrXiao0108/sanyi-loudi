package com.dzics.data.acquisition.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.common.model.custom.ReqWorkQrCodeOrder;
import com.dzics.common.model.response.Result;
import com.dzics.data.acquisition.config.MapConfig;
import com.dzics.data.acquisition.service.MomHttpRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author ZhangChengJun
 * Date 2021/6/11.
 * @since
 */
@Slf4j
@Service
public class MonHttpRequestServiceImpl implements MomHttpRequestService {

}
