package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
 * @since 2021-07-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_data_device")
@ApiModel(value="DzDataDevice对象", description="数据采集设备表")
public class DzDataDevice implements Serializable {

    private static final long serialVersionUID = 1L;



    @ExcelProperty("设备ID")
    @ApiModelProperty(value = "第三方设备id")
    @TableField("device_id")
    private Long deviceId;
    @ExcelProperty("公司代码")
    @ApiModelProperty(value = "公司代码")
    @TableField("company_code")
    private String companyCode;
    @ExcelProperty("工厂代码")
    @ApiModelProperty(value = "工厂代码")
    @TableField("factory_code")
    private String factoryCode;
    @ExcelProperty("设备名称")
    @ApiModelProperty(value = "设备名称")
    @TableField("device_name")
    private String deviceName;
    @ExcelProperty("设备类型")
    @ApiModelProperty(value = "设备类型（1.搬运机器人，2.焊接机器人，3.数控设备，4.清洗机，5.检测设备）")
    @TableField("device_type")
    private Integer deviceType;
    @ExcelProperty("设备类型编号")
    @ApiModelProperty(value = "设备类型编号")
    @TableField("device_type_code")
    private String deviceTypeCode;
    @ExcelProperty("资产编码")
    @ApiModelProperty(value = "资产编码")
    @TableField("assets_encoding")
    private String assetsEncoding;
    @ExcelProperty("系统型号")
    @ApiModelProperty(value = "系统型号")
    @TableField("system_product_name")
    private String systemProductName;

    @ExcelProperty("序列号")
    @ApiModelProperty(value = "序列号")
    @TableField("ser_num")
    private String serNum;
    @ExcelProperty("软件版本")
    @ApiModelProperty(value = "软件版本")
    @TableField("nc_ver")
    private String ncVer;
    @ExcelProperty("焊接类型")
    @ApiModelProperty(value = "焊接类型(1.连续焊，2.组对点）")
    @TableField("soldering_type")
    private Integer solderingType;


    @ExcelIgnore
    @TableField("device_type_def")
    @ApiModelProperty("发送到IOT设备的设备类型 M表示磨床")
    private String deviceTypeDef;

    @ExcelIgnore
    @ApiModelProperty(value = "大正设备id")
    @TableField("equipment_id")
    private Long equipmentId;
    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;
    @ExcelIgnore
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;
    @ExcelIgnore
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;
    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @ExcelIgnore
    @ApiModelProperty(value = "主键")
    @TableId(value = "device_key", type = IdType.AUTO)
    private Long deviceKey;

    @ExcelIgnore
    @ApiModelProperty(value = "是否属于内部")
    @TableField(value = "is_inside" ,fill = FieldFill.DEFAULT)
    private Integer isInside;
}
