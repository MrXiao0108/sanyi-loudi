package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author xnb
 * @date 2021年11月04日 14:28
 */
@Data
public class AddFeedingVo {
    /**
     * 产线Id
     * */
    @ApiModelProperty("产线Id")
    @NotNull(message = "产线Id必选")
    private String lineId;

    /**
     * 投料点编号
     * */
    @ApiModelProperty("投料点编号")
//    @NotNull(message = "投料点编号必填")
    private String externalCode;

    /**
     * 投料点区域
     * */
    @ApiModelProperty("投料点区域")
//    @NotNull(message = "投料点区域必填")
    private String externalRegion;

    /**
     * 线路节点
     * */
    @ApiModelProperty("线路节点")
//    @NotNull(message = "线路节点必填")
    private String lineNode;

    /**
     * 小车名称
     * */
    @ApiModelProperty("小车名称")
//    @NotNull(message = "小车名称必填")
    private String inIslandCode;

    /**
     * 绑定工位
     * */
    @ApiModelProperty("绑定工位")
//    @NotNull(message = "绑定工位必选")
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
