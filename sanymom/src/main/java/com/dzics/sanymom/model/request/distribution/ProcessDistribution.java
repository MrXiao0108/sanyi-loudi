package com.dzics.sanymom.model.request.distribution;

import lombok.Data;

/**
 * 工序间配送
 */
@Data
public class ProcessDistribution {
    /**
     * Copyright 2021 bejson.com
     */


        private String taskType;
        private ProcessReported reported;
        private String version;
        private String taskId;

}
