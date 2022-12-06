package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author xnb
 * @date 2021年11月22日 9:18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("job_execution_log")
@ApiModel(value = "DzJobExecutionLog对象", description = "作业历史轨迹表")
public class DzJobExecutionLog {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @ApiModelProperty("主键")
    private String id;

    @TableField("job_name")
    @ApiModelProperty("作业名称")
    private String jobName;

    @TableField("task_id")
    @ApiModelProperty("任务名称,每次作业运行生成新任务")
    private String taskId;

    @TableField("hostname")
    @ApiModelProperty("主机名称")
    private String hostname;

    @TableField("ip")
    @ApiModelProperty("主机IP")
    private String ip;

    @TableField("sharding_item")
    @ApiModelProperty("分片项")
    private int shardingItem;

    @TableField("execution_source")
    @ApiModelProperty("作业执行来源。可选值为NORMAL_TRIGGER, MISFIRE, FAILOVER")
    private String executionSource;

    @TableField("failure_cause")
    @ApiModelProperty("执行失败原因")
    private String failureCause;

    @TableField("is_success")
    @ApiModelProperty("是否执行成功")
    private int isSuccess;

    @TableField("start_time")
    @ApiModelProperty("作业开始执行时间")
    private Date startTime;

    @TableField("complete_time")
    @ApiModelProperty("作业结束执行时间")
    private Date completeTime;
}
