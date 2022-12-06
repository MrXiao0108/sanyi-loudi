package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 设备检测数据V2新版记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_workpiece_data")
@ApiModel(value = "DzWorkpieceData对象", description = "设备检测数据V2新版记录")
public class DzWorkpieceData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    /**
     * 设备类型
     */
    @TableField("equipment_type")
    private Integer equipmentType;

    /**
     *
     *设备序号
     */
    @TableField("equipment_no")
    private String equipmentNo;
    /**
     * 产品编号
     */
    @ApiModelProperty(value = "产品编号")
    @TableField("product_no")
    private String productNo;

    /**
     * 产品ID
     */
    @TableField(exist = false)
    private String productId;

    @ApiModelProperty(value = "条形码")
    @TableField("produc_barcode")
    private String producBarcode;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField("name")
    private String name;

    /**
     * 工位编号
     */
    @ApiModelProperty(value = "工位编号")
    @TableField("work_number")
    private String workNumber;

    /**
     * 机床编号
     */
    @ApiModelProperty("机床编号")
    @TableField("machine_number")
    private String machineNumber;
    /**
     * 订单编号
     */
    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 交换码(验证码)
     */
    @TableField("auth_code")
    private String authCode;
    /**
     * 二维码
     */
    @TableField("qr_code")
    private String qrCode;

    /**
     * 产线编号
     */
    @TableField("line_no")
    private String lineNo;


    @ApiModelProperty(value = "检测日期")
    @TableField("date")
    private LocalDate date;

    @TableField("check_month")
    private String checkMonth;
    @ApiModelProperty(value = "创建日期")
    @TableField("detector_time")
    private Date detectorTime;

    @ApiModelProperty("创建日期")
    @TableField("create_time")
    private Date createTime;

    @ApiModelProperty(value = "总输出")
    @TableField("out_ok")
    private Integer outOk;

    @ApiModelProperty(value = "检测")
    @TableField("detect01")
    private BigDecimal detect01;

    @ApiModelProperty(value = "检测结果")
    @TableField("out_ok01")
    private Integer outOk01;

    @TableField("detect02")
    private BigDecimal detect02;

    @TableField("out_ok02")
    private Integer outOk02;

    @TableField("detect03")
    private BigDecimal detect03;

    @TableField("out_ok03")
    private Integer outOk03;

    @TableField("detect04")
    private BigDecimal detect04;

    @TableField("out_ok04")
    private Integer outOk04;

    @TableField("detect05")
    private BigDecimal detect05;

    @TableField("out_ok05")
    private Integer outOk05;

    @TableField("detect06")
    private BigDecimal detect06;

    @TableField("out_ok06")
    private Integer outOk06;

    @TableField("detect07")
    private BigDecimal detect07;

    @TableField("out_ok07")
    private Integer outOk07;

    @TableField("detect08")
    private BigDecimal detect08;

    @TableField("out_ok08")
    private Integer outOk08;

    @TableField("detect09")
    private BigDecimal detect09;

    @TableField("out_ok09")
    private Integer outOk09;

    @TableField("detect10")
    private BigDecimal detect10;

    @TableField("out_ok10")
    private Integer outOk10;

    @TableField("detect11")
    private BigDecimal detect11;

    @TableField("out_ok11")
    private Integer outOk11;

    @TableField("detect12")
    private BigDecimal detect12;

    @TableField("out_ok12")
    private Integer outOk12;

    @TableField("detect13")
    private BigDecimal detect13;

    @TableField("out_ok13")
    private Integer outOk13;

    @TableField("detect14")
    private BigDecimal detect14;

    @TableField("out_ok14")
    private Integer outOk14;

    @TableField("detect15")
    private BigDecimal detect15;

    @TableField("out_ok15")
    private Integer outOk15;

    @TableField("detect16")
    private BigDecimal detect16;

    @TableField("out_ok16")
    private Integer outOk16;

    @TableField("detect17")
    private BigDecimal detect17;

    @TableField("out_ok17")
    private Integer outOk17;

    @TableField("detect18")
    private BigDecimal detect18;

    @TableField("out_ok18")
    private Integer outOk18;

    @TableField("detect19")
    private BigDecimal detect19;

    @TableField("out_ok19")
    private Integer outOk19;

    @TableField("detect20")
    private BigDecimal detect20;

    @TableField("out_ok20")
    private Integer outOk20;

    @TableField("detect21")
    private BigDecimal detect21;

    @TableField("out_ok21")
    private Integer outOk21;

    @TableField("detect22")
    private BigDecimal detect22;

    @TableField("out_ok22")
    private Integer outOk22;

    @TableField("detect23")
    private BigDecimal detect23;

    @TableField("out_ok23")
    private Integer outOk23;

    @TableField("detect24")
    private BigDecimal detect24;

    @TableField("out_ok24")
    private Integer outOk24;

    @TableField("detect25")
    private BigDecimal detect25;

    @TableField("out_ok25")
    private Integer outOk25;

    @TableField("detect26")
    private BigDecimal detect26;

    @TableField("out_ok26")
    private Integer outOk26;

    @TableField("detect27")
    private BigDecimal detect27;

    @TableField("out_ok27")
    private Integer outOk27;

    @TableField("detect28")
    private BigDecimal detect28;

    @TableField("out_ok28")
    private Integer outOk28;
}
