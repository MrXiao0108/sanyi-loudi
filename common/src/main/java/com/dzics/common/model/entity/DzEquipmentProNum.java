package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.model.custom.CmdTcp;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备生产数量表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_pro_num")
@ApiModel(value = "DzEquipmentProNum对象", description = "班次生产记录表")
public class DzEquipmentProNum implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

    @ApiModelProperty("每日排班id")
    @TableField("day_id")
    private Long dayId;

    @ApiModelProperty("订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("产线序号")
    @TableField("line_no")
    private String lineNo;
    /**
     * 设备id
     */
    @ApiModelProperty("涉笔id")
    @TableField("equiment_id")
    private Long equimentId;

    @ApiModelProperty(value = "批次号")
    @TableField("batch_number")
    private String batchNumber;

    @ApiModelProperty(value = "产品编码")
    @TableField("model_number")
    private String modelNumber;

    @ApiModelProperty("产品类型")
    @TableField("product_type")
    private String productType;
    /**
     * 毛坯数量
     */
    @ApiModelProperty("毛坯数量")
    @TableField("rough_num")
    private Long roughNum;

    /**
     * 合格数量
     */
    @ApiModelProperty("合格数量")
    @TableField("qualified_num")
    private Long qualifiedNum;

    /**
     * 当前产量
     */
    @ApiModelProperty(value = "当前产量")
    @TableField("now_num")
    private Long nowNum;

    /**
     * 生产总量
     */
    @ApiModelProperty(value = "生产总量")
    @TableField("total_num")
    private Long totalNum;

    /**
     * 不良品数量
     */
    @ApiModelProperty("不良品数量")
    @TableField("badness_num")
    private Long badnessNum;

    /**
     * 工作日期
     */
    @TableField("work_data")
    private LocalDate workData;

    @TableField("work_year")
    private Integer workYear;

    @TableField("work_mouth")
    private String workMouth;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

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


    @ApiModelProperty("小时")
    @TableField("work_hour")
    private Integer workHour;
    /**
     * 设备生产的工件编号,工件名称 的指令信息
     */
    @TableField(exist = false)
    private List<CmdTcp> cmdTcpList;

    public DzEquipmentProNum() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDayId() {
        return dayId;
    }

    public void setDayId(Long dayId) {
        this.dayId = dayId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Long getRoughNum() {
        return roughNum;
    }

    public void setRoughNum(Long roughNum) {
        this.roughNum = roughNum;
    }

    public Long getQualifiedNum() {
        return qualifiedNum;
    }

    public void setQualifiedNum(Long qualifiedNum) {
        this.qualifiedNum = qualifiedNum;
    }

    public Long getNowNum() {
        return nowNum;
    }

    public void setNowNum(Long nowNum) {
        this.nowNum = nowNum;
    }

    public Long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Long totalNum) {
        this.totalNum = totalNum;
    }

    public Long getBadnessNum() {
        return badnessNum;
    }

    public void setBadnessNum(Long badnessNum) {
        this.badnessNum = badnessNum;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Boolean getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Boolean delFlag) {
        this.delFlag = delFlag;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
