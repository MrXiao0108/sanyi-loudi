/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dzics.business.controller.zookeeper;

import com.dzics.business.common.annotation.OperLog;
import com.dzics.business.zookeeperjob.service.JobAPIService;
import com.dzics.business.zookeeperjob.util.SessionRegistryCenterConfiguration;
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.JobBriefInfo;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ServerBriefInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Server operation RESTful API.
 */
@Api(tags = "服务器维度")
@RestController
@RequestMapping("/api/servers")
public  class ServerOperationController {

    @Autowired
    private JobAPIService jobAPIService;


    /**
     * Get servers total count.
     *
     * @return servers total count
     */
    @ApiOperation(value = "服务器总数")
    @ApiOperationSupport(author = "观书")
    @GetMapping("/count")
    public Result getServersTotalCount() {
        int serversTotalCount = jobAPIService.getServerStatisticsAPI().getServersTotalCount();
        return Result.ok(serversTotalCount);
    }

    /**
     * Get all servers brief info.
     *
     * @return all servers brief info
     */
    @ApiOperation(value = "服务器维度列表")
    @ApiOperationSupport(author = "观书")
    @GetMapping("/getAllServersBriefInfo")
    public Result<Collection<ServerBriefInfo>> getAllServersBriefInfo() {
        Collection<ServerBriefInfo> data = Objects.nonNull(SessionRegistryCenterConfiguration.getRegistryCenterConfiguration()) ?
                jobAPIService.getServerStatisticsAPI().getAllServersBriefInfo() : Collections.emptyList();
        return Result.ok(data);
    }

    /**
     * Disable server.
     *
     * @param serverIp server IP address
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "失效", operatorType = "后台")
    @ApiOperation(value = "失效")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/{serverIp}/disable")
    public Result<Boolean> disableServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().disable(null, serverIp);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * Enable server.
     *
     * @param serverIp server IP address
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "生效", operatorType = "后台")
    @ApiOperation(value = "生效")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/{serverIp}/enable")
    public Result<Boolean> enableServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().enable(null, serverIp);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * Shutdown server.
     *
     * @param serverIp server IP address
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "终止", operatorType = "后台")
    @ApiOperation(value = "终止")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/{serverIp}/shutdown")
    public Result<Boolean> shutdownServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().shutdown(null, serverIp);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * Remove server.
     *
     * @param serverIp server IP address
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除")
    @ApiOperationSupport(author = "观书")
    @DeleteMapping("/{serverIp:.+}")
    public Result<Boolean> removeServer(@PathVariable("serverIp") final String serverIp) {
        jobAPIService.getJobOperatorAPI().remove(null, serverIp);
        return Result.ok(Boolean.TRUE);
    }


    /**
     * Get jobs.
     *
     * @param serverIp server IP address
     * @return Job brief info
     */
    @ApiOperation(value = "详情->详情")
    @ApiOperationSupport(author = "观书")
    @GetMapping(value = "/{serverIp}/jobs")
    public Result<Collection<JobBriefInfo>> getJobs(@PathVariable("serverIp") final String serverIp) {
        Collection<JobBriefInfo> data = jobAPIService.getJobStatisticsAPI().getJobsBriefInfo(serverIp);
        return Result.ok(data);
    }

    /**
     * Disable server job.
     *
     * @param serverIp server IP address
     * @param jobName  job name
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "详情->失效", operatorType = "后台")
    @ApiOperation(value = "详情->失效")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/{serverIp}/jobs/{jobName}/disable")
    public Result<Boolean> disableServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().disable(jobName, serverIp);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * Enable server job.
     *
     * @param serverIp server IP address
     * @param jobName  job name
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "详情->生效", operatorType = "后台")
    @ApiOperation(value = "详情->生效")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/{serverIp}/jobs/{jobName}/enable")
    public Result<Boolean> enableServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().enable(jobName, serverIp);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * Shutdown server job.
     *
     * @param serverIp server IP address
     * @param jobName  job name
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "详情->终止", operatorType = "后台")
    @ApiOperation(value = "详情->终止")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/{serverIp}/jobs/{jobName}/shutdown")
    public Result<Boolean> shutdownServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().shutdown(jobName, serverIp);
        return Result.ok(Boolean.TRUE);
    }

    /**
     * Remove server job.
     *
     * @param serverIp server IP address
     * @param jobName  job name
     */
    @OperLog(operModul = "服务器维度", operType = OperType.OTHER, operDesc = "详情->删除", operatorType = "后台")
    @ApiOperation(value = "详情->删除")
    @ApiOperationSupport(author = "观书")
    @DeleteMapping("/{serverIp}/jobs/{jobName:.+}")
    public Result<Boolean> removeServerJob(@PathVariable("serverIp") final String serverIp, @PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().remove(jobName, serverIp);
        return Result.ok(Boolean.TRUE);
    }
}
