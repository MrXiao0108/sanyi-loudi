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
import com.dzics.common.enums.OperType;
import com.dzics.common.model.response.Result;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shardingsphere.elasticjob.infra.pojo.JobConfigurationPOJO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Job configuration RESTful API.
 */
@Api(tags = "作业配置")
@RestController
@RequestMapping("/api/jobs/config")
public  class JobConfigController {

    @Autowired
    private JobAPIService jobAPIService;
    
    /**
     * Get job configuration.
     *
     * @param jobName job name
     * @return job configuration
     */
    @ApiOperation(value = "获取作业配置")
    @ApiOperationSupport(author = "观书")
    @GetMapping(value = "/{jobName:.+}")
    public Result<JobConfigurationPOJO> getJobConfig(@PathVariable("jobName") final String jobName) {
        JobConfigurationPOJO data = jobAPIService.getJobConfigurationAPI().getJobConfiguration(jobName);
        data.setJobExtraConfigurations(null);
        return Result.ok(data);
    }
    
    /**
     * Update job configuration.
     *
     * @param jobConfiguration job configuration
     */
    @OperLog(operModul = "作业配置", operType = OperType.UPDATE, operDesc = "修改", operatorType = "后台")
    @ApiOperation(value = "更新作业配置")
    @ApiOperationSupport(author = "观书")
    @PutMapping
    public Result<Boolean> updateJobConfig(@RequestBody final JobConfigurationPOJO jobConfiguration) {
        jobConfiguration.setJobExtraConfigurations(jobAPIService.getJobConfigurationAPI().getJobConfiguration(jobConfiguration.getJobName()).getJobExtraConfigurations());
        jobAPIService.getJobConfigurationAPI().updateJobConfiguration(jobConfiguration);
        return Result.ok(Boolean.TRUE);
    }
    
    /**
     * Remove job configuration.
     *
     * @param jobName job name
     */
    @OperLog(operModul = "作业配置", operType = OperType.DEL, operDesc = "删除", operatorType = "后台")
    @ApiOperation(value = "删除")
    @ApiOperationSupport(author = "观书")
    @DeleteMapping("/{jobName:.+}")
    public Result<Boolean> removeJob(@PathVariable("jobName") final String jobName) {
        jobAPIService.getJobConfigurationAPI().removeJobConfiguration(jobName);
        return Result.ok(Boolean.TRUE);
    }
}
