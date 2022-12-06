package com.dzics.common.model.request;

import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.util.PageLimit;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SelectEquipmentVo extends PageLimit {

    @ApiModelProperty("站点名称")
    private String departName;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("类型(1检测设备,2机床,3机器人)")
    private Integer equipmentType;

    @ApiModelProperty("此参数不填")
    private String useOrgCode;

    @ApiModelProperty("设备名称")
    private String equipmentName;

    @ApiModelProperty("产线名称")
    private String lineName;

    @ApiModelProperty("产线Id")
    private String lineId;

    @ApiModelProperty("机器人编码")
    private String equipmentCode;

    @ApiModelProperty("设备序号")
    private String equipmentNo;

    private String tableKey;

    @ApiModelProperty("排序字段")
    private String field;
    @ApiModelProperty("ASC OR DESC OR 空字符串")
    private String type;
}
