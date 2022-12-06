package com.dzics.mqtt.model.entity;

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
 * 数据采集设备表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_data_device")
@ApiModel(value="DzDataDevice对象", description="数据采集设备表")
public class DzDataDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "device_key", type = IdType.AUTO)
    private Long deviceKey;

    @ApiModelProperty(value = "第三方设备id")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty(value = "公司代码")
    @TableField("company_code")
    private String companyCode;

    @ApiModelProperty(value = "工厂代码")
    @TableField("factory_code")
    private String factoryCode;

    @ApiModelProperty(value = "设备名称")
    @TableField("device_name")
    private String deviceName;

    /**
     * 1 搬运机器人
     * 2 焊接机器人
     * 3 数控设备
     * 4 清洗机
     * 5 检测设备
     *
     */
    @ApiModelProperty(value = "设备类型")
    @TableField("device_type")
    private String deviceType;

    @ApiModelProperty(value = "设备类型编号")
    @TableField("device_type_code")
    private String deviceTypeCode;

    @ApiModelProperty(value = "资产编码")
    @TableField("assets_encoding")
    private String assetsEncoding;

    @ApiModelProperty(value = "系统型号")
    @TableField("system_product_name")
    private String systemProductName;

    @TableField("device_type_def")
    @ApiModelProperty("发送到IOT设备的设备类型 M表示磨床")
    private String deviceTypeDef;

    @ApiModelProperty(value = "大正设备id")
    @TableField("equipment_id")
    private Long equipmentId;

    /**
     * 序列号
     */
    @TableField("ser_num")
    private String serNum;

    /**
     * 软件版本
     */
    @TableField("nc_ver")
    private String ncVer;


    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "是否属于内部")
    @TableField(value = "is_inside" ,fill = FieldFill.DEFAULT)
    private Integer isInside;

}
