package com.dzics.common.model.response.role;

import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.aop.Dict;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 站点公司表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ResSysDepart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "父机构ID")
    @JsonIgnore
    private String parentId;

    @ApiModelProperty(value = "站点公司名称")
    private String departName;

    @ApiModelProperty(value = "英文名")
    @JsonIgnore
    private String departNameEn;

    @ApiModelProperty(value = "缩写")
    @JsonIgnore
    private String departNameAbbr;

    @ApiModelProperty(value = "排序")
    private Integer departOrder;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "机构编码")
    private String orgCode;

    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "状态（1启用，0不启用）")
    private Integer status;

    @Dict(dicDataSource="sex")
    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
    @JsonIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新日期")
    private Date updateTime;


}
