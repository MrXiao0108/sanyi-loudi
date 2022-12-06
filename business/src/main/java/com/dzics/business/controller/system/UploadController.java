package com.dzics.business.controller.system;

import com.dzics.business.service.BusinessUserService;
import com.dzics.business.util.SnowflakeUtil;
import com.dzics.common.model.request.ReqUploadBase64;
import com.dzics.common.model.response.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"上传文件"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/user/uplaod")
public class UploadController {
    @Autowired
    BusinessUserService businessUserService;

    @Autowired
    public SnowflakeUtil snowflakeUtil;
    @ApiOperation(value = "上传文件")
    @PostMapping(headers = {"content-type=multipart/form-data"})
    public Result putAvatar(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @RequestParam("files") MultipartFile[] files
    ) {
        Result msg = businessUserService.putUpload(sub, files);
        return msg;
    }

    @ApiOperation(value = "上传文件BASE64")
    @PostMapping(value = "/base64")
    public Result putAvatarBase64(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                  @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                                  @RequestBody ReqUploadBase64 files) {
        Result msg = businessUserService.putAvatarBase64(sub, files);
        return msg;
    }
}
