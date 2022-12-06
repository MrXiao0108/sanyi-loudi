package com.dzics.business.api.http;

import com.dzics.business.service.KbParmsService;
import com.dzics.common.model.request.kb.KbParms;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 设备生产数量信息
 *
 * @author ZhangChengJun
 * Date 2021/4/26.
 * @since
 */
@Api(tags = {"接口组"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/methods")
@Controller
public class DeviceProQuantityController {
    @Autowired
    private KbParmsService kbParmsService;


    @ApiOperation(value = "接口组调用")
    @ApiOperationSupport(author = "NeverEnd")
    @PostMapping
    public Result getDeviceproductionQuantity(@Valid @RequestBody KbParms kbParms) {
        return kbParmsService.getMethodsGroup(kbParms);
    }

}
