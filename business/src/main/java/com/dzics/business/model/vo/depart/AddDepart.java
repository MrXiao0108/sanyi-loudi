package com.dzics.business.model.vo.depart;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增站点参数
 *
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
@Data
public class AddDepart {
    @ApiModelProperty(value = "站点公司名称")
    @NotBlank(message = "站点公司名称必填")
    private String departName;

    @ApiModelProperty(value = "排序")
    private Integer departOrder;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "机构编码")
    @NotBlank(message = "机构编码必填")
    private String orgCode;

    @ApiModelProperty(value = "备注")
    private String memo;

    @ApiModelProperty(value = "状态（1启用，0不启用）")
    @NotNull(message = "状态必传")
    private Integer status;

    @ApiModelProperty(value = "权限id")
    private List<Long> permissionId;
}
