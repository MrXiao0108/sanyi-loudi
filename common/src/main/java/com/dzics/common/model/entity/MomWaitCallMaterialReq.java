package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 生产叫料待完成请求
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_wait_call_material_req")
@ApiModel(value = "MomWaitCallMaterialReq对象", description = "生产叫料待完成请求")
public class MomWaitCallMaterialReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "wait_material_id_req", type = IdType.ASSIGN_ID)
    private String waitMaterialIdReq;

    /**
     * 等待叫料的订单 ID
     */
    @TableField("wait_material_id")
    private String waitMaterialId;

    @ApiModelProperty(value = "请求ID")
    @TableField("reqId")
    private String reqid;

    @ApiModelProperty(value = "系统编码")
    @TableField("reqSys")
    private String reqsys;

    /**
     * 产品物料号
     */
    @TableField("product_no")
    private String productNo;

    @ApiModelProperty("1产品物料 2 组件物料")
    @TableField("material_type")
    private String materialType;

    @ApiModelProperty(value = "顺序号")
    @TableField("SequenceNo")
    private String sequenceno;

    @ApiModelProperty(value = "工序号")
    @TableField("OprSequenceNo")
    private String oprsequenceno;

    @ApiModelProperty(value = "操作编码 0：请求来料，1：请求具体某框物料")
    @TableField("reqType")
    private String reqtype;

    /**
     * 机器人请求的小车编号
     */
    @TableField("basket_type")
    private String basketType;

    @ApiModelProperty(value = "料框类型")
    @TableField("palletType")
    private String pallettype;

    /**
     * 上料点编码
     */
    @ApiModelProperty(value = "上料点编码")
    @TableField("sourceNo")
    private String sourceno;

    @ApiModelProperty(value = "生产订单号")
    @TableField("wipOrderNo")
    private String wiporderno;

    @ApiModelProperty(value = "需求时间")
    @TableField("requireTime")
    private Date requiretime;

    @ApiModelProperty(value = "发送时间")
    @TableField("sendTime")
    private Date sendtime;

    @ApiModelProperty(value = "物料清单  jsonArray[] 1.请求物料清单列表,reqType为0时必填,reqType为1时选填；")
    @TableField("materialList")
    private String materiallist;

    @ApiModelProperty(value = "1 发送成功 0 未发送 或发送错误")
    @TableField("req_status")
    private String reqStatus;

    @ApiModelProperty(value = "叫料请求状态 0：订单已执行1：订单起点完成 2：订单终点完成 3：指令下发AGV成功 4：指令下发AGV失败")
    @TableField("order_status")
    private String orderStatus;

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


}
