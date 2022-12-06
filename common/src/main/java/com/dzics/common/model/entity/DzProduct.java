package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.model.response.ImgDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 产品列表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_product")
@ApiModel(value = "DzProduct对象", description = "产品列表")
public class DzProduct implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    @TableId(value = "product_id", type = IdType.AUTO)
    @ExcelIgnore
    private Long productId;

    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    @ExcelIgnore
    private Long orderId;

    @ApiModelProperty(value = "订单编码")
    @TableField("order_no")
    @ExcelIgnore
    private String orderNo;

    @ApiModelProperty(value = "站点id")
    @TableField("depart_id")
    @ExcelIgnore
    private Long departId;

    @ApiModelProperty(value = "站点编码")
    @TableField("depart_org_code")
    @ExcelIgnore
    private String departOrgCode;

    @ApiModelProperty("生产节拍")
    @TableField("frequency")
    private BigDecimal frequency;

    @ApiModelProperty(value = "产品名称")
    @TableField("product_name")
    @ExcelProperty("产品名称")
    private String productName;

    @ApiModelProperty(value = "产品序号，对接底层定义值使用,产品id")
    @TableField("product_no")
    @ExcelProperty("产品编号")
    private String productNo;

    @TableField("sy_category")
    @ApiModelProperty("三一产品类别")
    private String syCategory;

    @TableField("sy_product_alias")
    @ApiModelProperty("三一产品简码")
    private String syProductAlias;

    @ApiModelProperty("三一产品物料编码")
    @TableField("sy_productNo")
    private String syProductNo;

    @ApiModelProperty("图片")
    @TableField("picture")
    @ExcelIgnore
    private String picture;

    @ExcelProperty(value = "缩略图")
    @TableField(exist = false)
    private URL url;

    @ApiModelProperty("备注")
    @TableField("remarks")
    @ExcelIgnore
    private String remarks;

    @ApiModelProperty(value = "创建数据机构编码")
    @TableField("org_code")
    @ExcelIgnore
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    @ExcelIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @ExcelIgnore
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelIgnore
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @ExcelIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Date updateTime;

    /**
     * 产品类型 对应产线制作的类型
     */
    @ApiModelProperty(value = "产线类型(2米活塞杆=2HSG，3米活塞杆=3HSG，2米缸筒=2GT，3米钢筒=3GT)")
    @TableField("line_type")
    private String lineType;


    @ApiModelProperty("图片数组")
    @TableField(exist = false)
    @ExcelIgnore
    private List<ImgDo> pictureList;

    @ApiModelProperty("图片数组")
    @TableField(exist = false)
    @ExcelIgnore
    private List<DzMaterial> materialList;

}
