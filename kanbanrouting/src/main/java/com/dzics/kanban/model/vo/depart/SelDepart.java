package com.dzics.kanban.model.vo.depart;

import com.baomidou.mybatisplus.annotation.TableField;

import com.dzics.kanban.annotation.QueryType;
import com.dzics.kanban.enums.QueryTypeEnu;
import com.dzics.kanban.model.vo.base.SearchTimeBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户列表传递参数
 *
 * @author ZhangChengJun
 * Date 2021/1/12.
 * @since
 */
@Data
public class SelDepart extends SearchTimeBase {
    @ApiModelProperty(value = "站点公司名称")
    @QueryType(QueryTypeEnu.like)
    @TableField("depart_name")
    private String departName;
    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @QueryType(QueryTypeEnu.like)
    private String orgCode;
    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @TableField("status")
    private Integer status;


}
