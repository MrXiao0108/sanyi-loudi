package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectEquipmentStateVo {

    @ApiModelProperty(value = "订单号", required = true)
    @NotNull(message = "订单号必传")
    private String orderNo;
    @ApiModelProperty(value = "产线号", required = true)
    @NotNull(message = "产线号必传")
    private String lineNo;
    @ApiModelProperty(value = "设备序号")
    private String equipmentNo;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    private Integer equipmentType;

    @ApiModelProperty("搜索起始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    @ApiModelProperty("搜索结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
}
