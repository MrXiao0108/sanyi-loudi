package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzics.kanban.enums.OperType;
import com.dzics.kanban.enums.OperTypeStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 操作日志
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_operation_logging")
@ApiModel(value = "SysOperationLogging对象", description = "操作日志")
public class SysOperationLogging implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志主键")
    @TableId(value = "id", type = IdType.AUTO)
//    @JsonIgnore
    private Long id;

    @ApiModelProperty(value = "模块标题")
    @TableField("title")
    private String title;

    @ApiModelProperty(value = "操作描述")
    @TableField("oper_desc")
    private String operDesc;

    @ApiModelProperty(value = "业务类型（0其它 1新增 2修改 3删除）")
    @TableField("business_type")
    private OperType businessType;

    @ApiModelProperty(value = "方法名称")
    @TableField("method")
    private String method;

    @ApiModelProperty(value = "请求方式")
    @TableField("request_method")
    private String requestMethod;

    @ApiModelProperty(value = "接口类型")
    @TableField("operator_type")
    private String operatorType;

    @ApiModelProperty(value = "操作人员")
    @TableField("oper_name")
    private String operName;

    @ApiModelProperty(value = "请求URL")
    @TableField("oper_url")
    private String operUrl;

    @ApiModelProperty(value = "主机地址")
    @TableField("oper_ip")
    private String operIp;

    @ApiModelProperty(value = "操作地点")
    @TableField("oper_location")
    private String operLocation;

    @ApiModelProperty(value = "请求参数")
    @TableField("oper_param")
    private String operParam;

    @ApiModelProperty("运行时间单位 ms 毫秒")
    @TableField("run_time")
    private Long runTime;
    @ApiModelProperty(value = "返回参数")
    @TableField("json_result")
    private String jsonResult;

    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    @TableField("status")
    private OperTypeStatus status;

    @ApiModelProperty(value = " 异常信息")
    @TableField("exc_message")
    private String excMessage;

    @ApiModelProperty(value = "异常名称")
    @TableField("error_msg")
    private String errorMsg;

    @ApiModelProperty(value = "操作时间")
    @TableField("oper_time")
    private Date operTime;

    @ApiModelProperty(value = "访问日期")
    @TableField("oper_date")
    private LocalDate operDate;

    @TableField("org_code")
    @ApiModelProperty("系统编码")
    private String orgCode;

}
