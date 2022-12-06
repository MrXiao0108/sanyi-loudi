package com.dzics.business.controller.zookeeper;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.zookeeperjob.domian.RegistryCenterConfiguration;
import com.dzics.business.zookeeperjob.service.JobAPIService;
import com.dzics.business.zookeeperjob.service.RegistryCenterConfigurationService;
import com.dzics.business.zookeeperjob.util.SessionRegistryCenterConfiguration;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ShardingInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.reg.RegistryCenterFactory;
import org.apache.shardingsphere.elasticjob.reg.exception.RegException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * 作业维度接口
 *
 * @author ZhangChengJun
 * Date 2021/11/22.
 * @since
 */
@Api(tags = "注册中心")
@RestController
@RequestMapping("/api/registry-center")
public class WorkCenterController {

    @Autowired
    private RegistryCenterConfigurationService regCenterService;

    public static final String REG_CENTER_CONFIG_KEY = "reg_center_config_key";

    @ApiOperation(value = "注册中心是否激活")
    @ApiOperationSupport(author = "观书")
    @GetMapping("/activated")
    public Result<RegistryCenterConfiguration> activated() {
        return Result.ok(regCenterService.loadActivated().orElse(null));
    }

    @OperLog(operModul = "注册中心", operType = OperType.OTHER, operDesc = "新增配置", operatorType = "后台")
    @ApiOperation(value = "新增配置")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody final RegistryCenterConfiguration config) {
        return Result.ok(regCenterService.add(config));
    }

    @ApiOperation(value = "获取配置")
    @ApiOperationSupport(author = "观书")
    @GetMapping("/load")
    public Result<Collection<RegistryCenterConfiguration>> load(final HttpServletRequest request) {
        regCenterService.loadActivated().ifPresent(regCenterConfig -> setRegistryCenterNameToSession(regCenterConfig, request.getSession()));
        return Result.ok(regCenterService.loadAll().getRegistryCenterConfiguration());
    }

    @OperLog(operModul = "注册中心", operType = OperType.OTHER, operDesc = "连接", operatorType = "后台")
    @ApiOperation(value = "连接")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/connect")
    public Result<Boolean> connect(@RequestBody final RegistryCenterConfiguration config, final HttpServletRequest request) {
        boolean isConnected = setRegistryCenterNameToSession(regCenterService.find(config.getName(), regCenterService.loadAll()), request.getSession());
        if (isConnected) {
            regCenterService.load(config.getName());
        }
        return Result.ok(isConnected);
    }
    private boolean setRegistryCenterNameToSession(final RegistryCenterConfiguration regCenterConfig, final HttpSession session) {
        session.setAttribute(REG_CENTER_CONFIG_KEY, regCenterConfig);
        try {
            RegistryCenterFactory.createCoordinatorRegistryCenter(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
            SessionRegistryCenterConfiguration.setRegistryCenterConfiguration((RegistryCenterConfiguration) session.getAttribute(REG_CENTER_CONFIG_KEY));
        } catch (final RegException ex) {
            return false;
        }
        return true;
    }


    @OperLog(operModul = "注册中心", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除")
    @ApiOperationSupport(author = "观书")
    @DeleteMapping
    public Result delete(@RequestBody final RegistryCenterConfiguration config) {
        regCenterService.delete(config.getName());
        return Result.ok();
    }

}
