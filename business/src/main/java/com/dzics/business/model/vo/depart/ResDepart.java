package com.dzics.business.model.vo.depart;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
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
    @ExcelProperty("站点名称")
    @ApiModelProperty(value = "站点公司名称")
    private String departName;
    @ExcelProperty("创建人")
    @ApiModelProperty(value = "创建人")
    private String createBy;
    @ExcelProperty("机构编码")
    @ApiModelProperty(value = "机构编码")
    private String orgCode;
    @ExcelProperty("创建日期")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    @ExcelIgnore
    @ApiModelProperty(value = "站点id")
    private String departId;
    @ExcelIgnore
    @ApiModelProperty(value = "排序")
    private Integer departOrder;
    @ExcelIgnore
    @ApiModelProperty(value = "描述")
    private String description;
    @ExcelIgnore
    @ApiModelProperty(value = "备注")
    private String memo;
    @ExcelIgnore
    @ApiModelProperty(value = "状态（1启用，0不启用）")
    private Integer status;
    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;


}
