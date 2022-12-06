package com.dzics.kanban.controller.system;

import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.service.BusinessUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

    @ApiOperation(value = "上传文件")
    @PostMapping(headers = {"content-type=multipart/form-data"})
    public Result putAvatar(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                            @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub,
                            @RequestParam("files") MultipartFile[] files
    ) {
        Result msg = businessUserService.putUpload(sub, files);
        return msg;
    }
}
