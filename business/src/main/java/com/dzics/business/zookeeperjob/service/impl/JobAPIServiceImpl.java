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

package com.dzics.business.zookeeperjob.service.impl;

import com.dzics.business.zookeeperjob.domian.RegistryCenterConfiguration;
import com.dzics.business.zookeeperjob.service.JobAPIService;
import com.dzics.business.zookeeperjob.util.SessionRegistryCenterConfiguration;
import org.apache.shardingsphere.elasticjob.lite.internal.instance.InstanceService;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.api.*;
import org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.reg.RegistryCenterFactory;
import org.apache.shardingsphere.elasticjob.reg.base.CoordinatorRegistryCenter;
import org.springframework.stereotype.Service;

/**
 * Job API service implementation.
 */
@Service
public final class JobAPIServiceImpl implements JobAPIService {
    
    @Override
    public JobConfigurationAPI getJobConfigurationAPI() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return JobAPIFactory.createJobConfigurationAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public JobOperateAPI getJobOperatorAPI() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return new JobOperateAPIImpl(RegistryCenterFactory.createCoordinatorRegistryCenter(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest()));
    }
    
    @Override
    public ShardingOperateAPI getShardingOperateAPI() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return JobAPIFactory.createShardingOperateAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public JobStatisticsAPI getJobStatisticsAPI() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return JobAPIFactory.createJobStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public ServerStatisticsAPI getServerStatisticsAPI() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return JobAPIFactory.createServerStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    @Override
    public ShardingStatisticsAPI getShardingStatisticsAPI() {
        RegistryCenterConfiguration regCenterConfig = SessionRegistryCenterConfiguration.getRegistryCenterConfiguration();
        return JobAPIFactory.createShardingStatisticsAPI(regCenterConfig.getZkAddressList(), regCenterConfig.getNamespace(), regCenterConfig.getDigest());
    }
    
    private static class JobOperateAPIImpl implements JobOperateAPI {
        
        private final CoordinatorRegistryCenter regCenter;
        
        private final org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl delegated;
        
        public JobOperateAPIImpl(final CoordinatorRegistryCenter regCenter) {
            this.regCenter = regCenter;
            this.delegated = new org.apache.shardingsphere.elasticjob.lite.lifecycle.internal.operate.JobOperateAPIImpl(regCenter);
        }
        
        @Override
        public void trigger(final String jobName) {
            new InstanceService(regCenter, jobName).triggerAllInstances();
        }
        
        @Override
        public void disable(final String jobName, final String serverIp) {
            delegated.disable(jobName, serverIp);
        }
        
        @Override
        public void enable(final String jobName, final String serverIp) {
            delegated.enable(jobName, serverIp);
        }
        
        @Override
        public void shutdown(final String jobName, final String serverIp) {
            delegated.shutdown(jobName, serverIp);
        }
        
        @Override
        public void remove(final String jobName, final String serverIp) {
            delegated.remove(jobName, serverIp);
        }
    }
}
