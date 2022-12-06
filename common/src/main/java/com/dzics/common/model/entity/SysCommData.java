package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 设备上传的数据
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_comm_data")
@ApiModel(value = "SysCommData对象", description = "设备上传的数据")
public class SysCommData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;
    /**
     * 识别码
     */
    @ApiModelProperty(value = "识别码")
    @TableField("identification_code")
    private String identificationCode;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    @TableField("order_number")
    private String orderNumber;
    /**
     * 产线序号
     */
    @ApiModelProperty(value = "产线序号")
    @TableField("production_line_number")
    private String productionLineNumber;
    /**
     * 设备类型
     */
    @ApiModelProperty(value = "设备类型")
    @TableField("device_type")
    private String deviceType;
    /**
     * 设备ip
     */
    @ApiModelProperty(value = "设备ip")
    @TableField("device_ip")
    private String deviceIp;
    /**
     * 设备号
     */
    @ApiModelProperty(value = "设备号")
    @TableField("device_number")
    private String deviceNumber;

    /**
     * 机构编码
     */
    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    /**
     * 指令
     */
    @ApiModelProperty(value = "指令")
    @TableField("tcp_value")
    private String tcpValue;
    /**
     * 指令值
     */
    @ApiModelProperty(value = "指令值")
    @TableField("device_item_value")
    private String deviceItemValue;
    /**
     * 指令描述
     */
    @ApiModelProperty(value = "指令描述")
    @TableField("tcp_description")
    private String tcpDescription;
    /**
     * 删除状态(0正常 1删除
     */
    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    @TableField("del_flag")
    private Boolean delFlag;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    /**
     * 底层数据队列接收时间
     */
    @TableField(exist = false)
    private Date receivingTime;
}
