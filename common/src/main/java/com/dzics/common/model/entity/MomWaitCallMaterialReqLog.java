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
 * 叫料的订单日志记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_wait_call_material_req_log")
@ApiModel(value = "MomWaitCallMaterialReqLog对象", description = "叫料的订单日志记录")
public class MomWaitCallMaterialReqLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "wait_material_log_id", type = IdType.ASSIGN_ID)
    private String waitMaterialLogId;

    @ApiModelProperty("请求MOM的ID")
    @TableField("reqId")
    private String reqId;
    @ApiModelProperty("1产品物料 2 组件物料")
    @TableField("material_type")
    private String materialType;

    @ApiModelProperty("返回结果   0：正确；其它：错误   必填")
    @TableField("res_mom_code")
    private String resMomCode;

    @ApiModelProperty("响应信息")
    @TableField("res_msg")
    private String resMsg;

    @ApiModelProperty(value = "1 发送成功 0 发送失败")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "发送请求时间")
    @TableField("send_time")
    private Date sendTime;

    @ApiModelProperty(value = "发送请求参数")
    @TableField("send_msg")
    private String sendMsg;

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
