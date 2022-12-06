package com.dzics.business.model.vo.rolemenu;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.model.request.base.SearchTimeBase;
import com.dzics.common.annotation.QueryType;
import com.dzics.common.enums.QueryTypeEnu;
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
public class SelRole  extends SearchTimeBase {
    @ApiModelProperty(value = "角色名称")
    @QueryType(QueryTypeEnu.like)
    @TableField("username")
    private String roleName;

    @ApiModelProperty(value = "权限标识")
    @TableField("roleCode")
    @QueryType(QueryTypeEnu.like)
    private String roleCode;

    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @TableField("status")
    private Integer status;

}
