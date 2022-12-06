package com.dzics.kanban.model.vo.depart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 站点列表
 *
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
@Data
public class ResDepart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "站点id")
    private Long departId;

    @ApiModelProperty(value = "站点公司名称")
    private String departName;

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

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新日期")
    private Date updateTime;


}
