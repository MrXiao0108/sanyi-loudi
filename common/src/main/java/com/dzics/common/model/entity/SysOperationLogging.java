package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dzics.common.enums.OperType;
import com.dzics.common.enums.OperTypeStatus;
import com.dzics.common.model.write.LoginStatusConverter;
import com.dzics.common.model.write.OperTypeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @ExcelProperty("模块标题")
    @ApiModelProperty(value = "模块标题")
    @TableField("title")
    private String title;
    @ExcelProperty(value = "业务类型",converter = OperTypeConverter.class)
    @ApiModelProperty(value = "业务类型（0其它 1新增 2修改 3删除）")
    @TableField("business_type")
    private OperType businessType;
    @ExcelProperty(value = "操作状态",converter =LoginStatusConverter.class )
    @ApiModelProperty(value = "操作状态（0正常 1异常）")
    @TableField("status")
    private OperTypeStatus status;
    @ExcelProperty("请求方式")
    @ApiModelProperty(value = "请求方式")
    @TableField("request_method")
    private String requestMethod;
    @ExcelProperty("操作内容")
    @ApiModelProperty(value = "操作描述")
    @TableField("oper_desc")
    private String operDesc;
    @ExcelProperty("请求URL")
    @ApiModelProperty(value = "请求URL")
    @TableField("oper_url")
    private String operUrl;
    @ExcelProperty("耗时")
    @ApiModelProperty("运行时间单位 ms 毫秒")
    @TableField("run_time")
    private Long runTime;
    @ExcelProperty("操作人员")
    @ApiModelProperty(value = "操作人员")
    @TableField("oper_name")
    private String operName;
    @ExcelProperty("主机地址")
    @ApiModelProperty(value = "主机地址")
    @TableField("oper_ip")
    private String operIp;
    @ExcelProperty("操作时间")
    @ApiModelProperty(value = "操作时间")
    @TableField("oper_time")
    private Date operTime;


    @ExcelIgnore
    @ApiModelProperty(value = "日志主键")
    @TableId(value = "id", type =IdType.AUTO)
//    @JsonIgnore
    private Long id;
    @ExcelIgnore
    @ApiModelProperty(value = "方法名称")
    @TableField("method")
    private String method;
    @ExcelIgnore
    @ApiModelProperty(value = "接口类型")
    @TableField("operator_type")
    private String operatorType;
    @ExcelIgnore
    @ApiModelProperty(value = "操作地点")
    @TableField("oper_location")
    private String operLocation;
    @ExcelIgnore
    @ApiModelProperty(value = "请求参数")
    @TableField("oper_param")
    private String operParam;
    @ExcelIgnore
    @ApiModelProperty(value = "返回参数")
    @TableField("json_result")
    private String jsonResult;
    @ExcelIgnore
    @ApiModelProperty(value = " 异常信息")
    @TableField("exc_message")
    private String excMessage;
    @ExcelIgnore
    @ApiModelProperty(value = "异常名称")
    @TableField("error_msg")
    private String errorMsg;
    @ExcelIgnore
    @ApiModelProperty(value = "访问日期")
    @TableField(value = "oper_date")
    private LocalDate operDate;
    @ExcelIgnore
    @TableField("org_code")
    @ApiModelProperty("系统编码")
    private String orgCode;

}
