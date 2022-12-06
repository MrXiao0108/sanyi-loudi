package com.dzics.common.model.response.role;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
public class ResSysRole implements Serializable {
    @ExcelIgnore
    @ApiModelProperty("归属站点id")
    private String departId;
    @ExcelIgnore
    @ApiModelProperty(value = "主键id")
    private String roleId;

    @ExcelProperty(value = "用户账号")
    @ApiModelProperty(value = "角色名称")
    private String roleName;

    @ExcelProperty(value = "创建人")
    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ExcelProperty(value = "权限字符")
    @ApiModelProperty(value = "角色编码")
    private String roleCode;

    @ExcelProperty(value = "描述")
    @ApiModelProperty(value = "描述")
    private String description;

    @ExcelProperty(value = "创建时间")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;


    @ExcelIgnore
    @ApiModelProperty("是否禁用")
    private Boolean disabled;

    @ExcelIgnore
    @ApiModelProperty("1是基础角色")
    private Integer basicsRole;
    @ExcelIgnore
    @ApiModelProperty(value = "机构编码")
    private String orgCode;
    @ExcelIgnore
    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    private Integer status;
    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @JsonIgnore
    private Boolean delFlag;
    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    @JsonIgnore
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @JsonIgnore
    private Date updateTime;


}
