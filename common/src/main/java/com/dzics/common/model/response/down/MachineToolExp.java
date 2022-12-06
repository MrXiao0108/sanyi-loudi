package com.dzics.common.model.response.down;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.dzics.common.model.entity.DzEquipmentDowntimeRecord;
import com.dzics.common.model.write.ConversionSecond;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/7/7.
 * @since
 */
@Data
public class MachineToolExp {

    /**
     * 订单编号
     */
    @ExcelProperty(value = "订单编号")
    private String orderNo;

    @ExcelProperty("归属站点")
    private String departName;

    @ExcelProperty("产线名称")
    private String lineName;

    /**
     * 设备序号
     */
    @ExcelProperty("机床序号")
    private String equipmentNo;

    @ExcelProperty("机床编号")
    private String equipmentCode;

    @ExcelProperty("机床名称")
    private String equipmentName;

    @ExcelProperty(value = "累计停机时间", converter = ConversionSecond.class)
    private Long downTime;

    @ApiModelProperty("停机次数")
    @ExcelProperty("停机次数")
    private Long downSum;

}
