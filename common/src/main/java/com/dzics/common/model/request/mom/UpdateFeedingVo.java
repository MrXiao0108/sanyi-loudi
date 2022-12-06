package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author xnb
 * @date 2021年11月04日 14:28
 */
@Data
public class UpdateFeedingVo {
    @ApiModelProperty(value = "唯一键",required = true)
    @NotEmpty(message = "唯一主键必填")
    private String materialPointId;
    /**
     * 产线Id
     * */
    @ApiModelProperty(value = "产线ID必填")
    @NotEmpty(message = "产线ID必填")
    private String lineId;

    /**
     * 投料点编号
     * */
    @ApiModelProperty(value = "投料点编号必填")
    @NotEmpty(message = "投料点编号必填")
    private String externalCode;

    /**
     * 投料点区域
     * */
    @ApiModelProperty(value = "投料点区域必填")
    private String externalRegion;


    /**
     * 线路节点
     * */
    @ApiModelProperty(value = "线路节点必填")
    private String lineNode;

    /**
     * 小车名称
     * */
    @ApiModelProperty(value = "小车编号必填")
    @NotEmpty(message = "小车编号必填")
    private String inIslandCode;

    /**
     * 绑定工位
     * */
    @ApiModelProperty(value = "工位编号",required = true)
    @NotEmpty(message = "工位编号")
    private String stationName;


    /**
     * 料点模式
     *
     */
    @ApiModelProperty("料点模式, NG （NG物料） TL (退库)  正常 不填写,下拉框，传递的值 就是 NG 或 TL ,正常的不传递，或传递空字符串 ")
    private String pointModel;

    /**
     * 是否下料点
     * next_point
     */
    @ApiModelProperty("是否终点工序")
    private Boolean nextPoint;
}
