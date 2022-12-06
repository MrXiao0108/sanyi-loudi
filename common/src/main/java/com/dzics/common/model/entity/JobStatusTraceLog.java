package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author dzics
 * @since 2021-11-22
 */
@Data
public class JobStatusTraceLog implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ApiModelProperty("主键")
    private String id;
    @ApiModelProperty("作业名称")
    private String jobName;
    @ApiModelProperty("原任务名称")
    private String originalTaskId;
    @ApiModelProperty("任务名称")
    private String taskId;
    @ApiModelProperty("执行作业服务器的名称，Lite版本为服务器的IP地址，Cloud版本为Mesos执行机主键")
    private String slaveId;
    @ApiModelProperty("任务执行源，可选值为CLOUD_SCHEDULER, CLOUD_EXECUTOR, LITE_EXECUTOR")
    private String source;
    @ApiModelProperty("任务执行类型，可选值为NORMAL_TRIGGER, MISFIRE, FAILOVER")
    private String executionType;
    @ApiModelProperty("分片项集合，多个分片项以逗号分隔")
    private String shardingItem;
    @ApiModelProperty("任务执行状态，可选值为TASK_STAGING, TASK_RUNNING, TASK_FINISHED, TASK_KILLED, TASK_LOST, TASK_FAILED, TASK_ERROR")
    private String state;
    @ApiModelProperty("相关信息")
    private String message;
    @ApiModelProperty("记录创建时间")
    private LocalDateTime creationTime;



}
