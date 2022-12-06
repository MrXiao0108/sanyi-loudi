package com.dzics.mqtt.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("iot_send_data")
@ApiModel(value="IotSendData对象", description="")
public class IotSendData implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "刀具号")
    @TableField("TCode")
    private String tcode;

    @ApiModelProperty(value = "主轴负载")
    @TableField("SpinLoad1")
    private String spinload1;

    @ApiModelProperty(value = "主轴速度")
    @TableField("ActSpin")
    private String actspin;

    @ApiModelProperty(value = "切削速度")
    @TableField("ActFeed")
    private String actfeed;

    @ApiModelProperty(value = "主程序号")
    @TableField("MainPgm")
    private String mainpgm;

    @ApiModelProperty(value = "当前程序号")
    @TableField("CurPgm")
    private String curpgm;

    @ApiModelProperty(value = "程序行号")
    @TableField("CurSeq")
    private String curseq;

    @TableField("workState")
    private Integer workstate;

    @TableField("equipmentId")
    private Long equipmentid;

    @TableField("deviceId")
    private Long deviceid;

    @ApiModelProperty(value = "创建数据机构编码")
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
