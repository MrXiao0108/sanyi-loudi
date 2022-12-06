package com.dzics.common.model.response.mom;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xnb
 * @date 2021年11月04日 15:14
 */
@Data
public class GetFeedingAgvDo {
    @ApiModelProperty("主键")
    @ExcelIgnore
    private String materialPointId;

    @ApiModelProperty("产线Id")
    @ExcelIgnore
    private String lineId;

    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("投料点编号")
    @ExcelProperty("投料点编号")
    private String externalCode;

    @ApiModelProperty("投料点区域")
    @ExcelProperty("投料点区域")
    private String externalRegion;

    @ApiModelProperty("线路节点")
    @ExcelProperty("线路节点")
    private String lineNode;

    @ApiModelProperty("小车名称")
    @ExcelProperty("小车名称")
    private String inIslandCode;

    @ApiModelProperty("绑定工位")
    @ExcelProperty("工位编号")
    private String stationName;

    @ApiModelProperty("料点模式, NG （NG物料） TL (退库)  正常 不填写,下拉框，传递的值 就是 NG 或 TL ,正常的不传递，或传递空字符串 ")
    private String pointModel;

    @ApiModelProperty("dzics工位编号")
    private String dzStationCode;
    /**
     * 是否下料点
     * next_point
     */
    @ApiModelProperty("是否终点工序")
    private Boolean nextPoint;
}
