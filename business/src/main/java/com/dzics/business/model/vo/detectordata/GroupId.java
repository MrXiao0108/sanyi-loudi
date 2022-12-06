package com.dzics.business.model.vo.detectordata;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 检测设置分组id
 *
 * @author ZhangChengJun
 * Date 2021/2/6.
 * @since
 */
@Data
public class GroupId {
    @ApiModelProperty("检测设置id")
    private String groupId;
    @ApiModelProperty("站点id")
    private Long departId;
    @ApiModelProperty("产品序号")
    private String productNo;
    @ApiModelProperty("是否获取配置信息 true获取 false 不获取")
    private Boolean checkModel = true;
    @ApiModelProperty("是否是比对值编辑模式")
    private Boolean editingMode = false;
}
