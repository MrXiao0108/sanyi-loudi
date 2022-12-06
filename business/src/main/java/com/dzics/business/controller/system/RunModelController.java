package com.dzics.business.controller.system;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.model.vo.LockScreenPassWord;
import com.dzics.business.service.cache.DzDetectionTemplCache;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.dzics.common.model.response.RunDataModel;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZhangChengJun
 * Date 2021/2/23.
 * @since
 */
@Api(tags = {"系统设置"}, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequestMapping("/api/system/run/model")
@Controller
public class RunModelController {
    @Autowired
    private DzDetectionTemplCache dzDetectionTemplCache;

    @ApiOperation(value = "当前运行数据模式")
    @ApiOperationSupport(author = "NeverEnd", order = 999)
    @GetMapping
    public Result<?> systemRunModel(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                    @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        String result = ((RunDataModel) dzDetectionTemplCache.systemRunModel(sub).getData()).getRunDataModel();
        return Result.ok(result);
    }

    @OperLog(operModul = "系统设置", operType = OperType.UPDATE, operDesc = "修改运行模式", operatorType = "后台")
    @ApiOperation(value = "修改运行模式")
    @ApiOperationSupport(author = "NeverEnd", order = 998)
    @PutMapping
    public Result<?> editSystemRunModel(@RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        RunDataModel runDataModel = ((RunDataModel) dzDetectionTemplCache.systemRunModel(sub).getData());
        String result = ((RunDataModel) dzDetectionTemplCache.editSystemRunModel(sub, runDataModel).getData()).getRunDataModel();
        return Result.ok(result);
    }



    /**
     * 获取传输带控制锁屏密码
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "验证锁屏密码")
    @PostMapping("/lock/screen/password")
    public Result lockScreenPassword(@RequestBody @Valid LockScreenPassWord screenPassWord, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                     @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzDetectionTemplCache.getLockScreenPassword(screenPassWord,sub);
    }

    /**
     * 更新传输带控制锁屏密码
     *
     * @param tokenHdaer
     * @param sub
     * @return
     */
    @ApiOperation(value = "更新锁屏密码")
    @PutMapping("/update/lock/screen/password")
    public Result putLockScreenPassword(@RequestBody @Valid LockScreenPassWord screenPassWord, @RequestHeader(value = "jwt_token", required = false) @ApiParam(value = "token令牌", required = true) String tokenHdaer,
                                        @RequestHeader(value = "sub", required = false) @ApiParam(value = "用户账号", required = true) String sub) {
        return dzDetectionTemplCache.putLockScreenPassword(sub, screenPassWord);
    }
}
