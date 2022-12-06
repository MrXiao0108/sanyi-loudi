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
import org.apache.shardingsphere.elasticjob.lite.lifecycle.domain.ShardingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Job operation RESTful API.
 */
@Api(tags = "作业操作")
@RestController
@RequestMapping("/api/jobs")
public  class JobOperationController {
    @Autowired
    private JobAPIService jobAPIService;
    

    /**
     * Get jobs total count.
     * 
     * @return jobs total count
     */
    @ApiOperation(value = "作业总数")
    @ApiOperationSupport(author = "观书")
    @GetMapping("/count")
    public int getJobsTotalCount() {
        return jobAPIService.getJobStatisticsAPI().getJobsTotalCount();
    }
    
    /**
     * Get all jobs brief info.
     * 
     * @return all jobs brief info
     */

    @ApiOperation(value = "作业维度")
    @ApiOperationSupport(author = "观书")
    @GetMapping("/getAllJobsBriefInfo")
    public Result<Collection<JobBriefInfo>> getAllJobsBriefInfo() {
        Collection<JobBriefInfo> data =  Objects.nonNull(SessionRegistryCenterConfiguration.getRegistryCenterConfiguration()) ?
                jobAPIService.getJobStatisticsAPI().getAllJobsBriefInfo() : Collections.emptyList();
        return Result.ok(data);
    }
    
    /**
     * Trigger job.
     * 
     * @param jobName job name
     */
    @OperLog(operModul = "作业操作", operType = OperType.OTHER, operDesc = "触发", operatorType = "后台")
    @ApiOperation(value = "触发")
    @ApiOperationSupport(author = "观书")
    @PostMapping("/{jobName}/trigger")
    public Result<Boolean> triggerJob(@PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().trigger(jobName);
        return Result.ok(Boolean.TRUE);
    }
    
    /**
     * Disable job.
     * 
     * @param jobName job name
     */
    @OperLog(operModul = "作业操作", operType = OperType.OTHER, operDesc = "失效", operatorType = "后台")
    @ApiOperation(value = "失效")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/{jobName}/disable")
    public Result<Boolean> disableJob(@PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().disable(jobName, null);
        return Result.ok(Boolean.TRUE);
    }
    
    /**
     * Enable job.
     *
     * @param jobName job name
     */
    @OperLog(operModul = "作业操作", operType = OperType.OTHER, operDesc = "生效", operatorType = "后台")
    @ApiOperation("生效")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/{jobName}/enable")
    public Result<Boolean> enableJob(@PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().enable(jobName, null);
        return Result.ok(Boolean.TRUE);
    }
    
    /**
     * Shutdown job.
     * 
     * @param jobName job name
     */
    @OperLog(operModul = "作业操作", operType = OperType.OTHER, operDesc = "终止", operatorType = "后台")
    @ApiOperation("终止")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/{jobName}/shutdown")
    public Result<Boolean> shutdownJob(@PathVariable("jobName") final String jobName) {
        jobAPIService.getJobOperatorAPI().shutdown(jobName, null);
        return Result.ok(Boolean.TRUE);
    }
    
    /**
     * Get sharding info.
     * 
     * @param jobName job name
     * @return sharding info
     */
    @ApiOperation("详情->详情")
    @ApiOperationSupport(author = "观书")
    @GetMapping(value = "/{jobName}/sharding")
    public Result<Collection<ShardingInfo>> getShardingInfo(@PathVariable("jobName") final String jobName) {
        Collection<ShardingInfo> data =  jobAPIService.getShardingStatisticsAPI().getShardingInfo(jobName);
        return Result.ok(data);
    }
    
    /**
     * Disable sharding.
     *
     * @param jobName job name
     * @param item sharding item
     */
    @OperLog(operModul = "作业操作", operType = OperType.OTHER, operDesc = "详情->失效", operatorType = "后台")
    @ApiOperation("详情->失效")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/{jobName}/sharding/{item}/disable")
    public Result<Boolean> disableSharding(@PathVariable("jobName") final String jobName, @PathVariable("item") final String item) {
        jobAPIService.getShardingOperateAPI().disable(jobName, item);
        return Result.ok(Boolean.TRUE);
    }
    
    /**
     * Enable sharding.
     *
     * @param jobName job name
     * @param item sharding item
     */
    @OperLog(operModul = "作业操作", operType = OperType.OTHER, operDesc = "详情->生效", operatorType = "后台")
    @ApiOperation("详情->生效")
    @ApiOperationSupport(author = "观书")
    @PostMapping(value = "/{jobName}/sharding/{item}/enable")
    public Result<Boolean> enableSharding(@PathVariable("jobName") final String jobName, @PathVariable("item") final String item) {
        jobAPIService.getShardingOperateAPI().enable(jobName, item);
        return Result.ok(Boolean.TRUE);
    }
}
