package com.dzics.common.model.request.device.maintain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 保养设备新增参数
 *
 * @author ZhangChengJun
 * Date 2021/9/29.
 * @since
 */
@Data
public class AddMaintainDevice {
    @ApiModelProperty("保养设备ID")
    private String maintainId;

    @ApiModelProperty(value = "产线ID", required = true)
    @NotBlank(message = "产线ID必填")
    private String lineId;

    @ApiModelProperty(value = "设备ID", required = true)
    @NotBlank(message = "设备ID必填")
    private String deviceId;

    @ApiModelProperty(value = "出厂日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "出厂日期必填")
    private LocalDate dateOfProduction;
    @ApiModelProperty(value = "上次保养日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "上次保养日期必填")
    private LocalDate maintainDateBefore;
    @ApiModelProperty(value = "下次保养日期", required = true)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "下次保养日期必填")
    private LocalDate maintainDateAfter;

    @ApiModelProperty(value = "年检类型 年 月 周", required = true)
    @NotBlank(message = "单位必填")
    private String unit;

    @ApiModelProperty(value = "单位倍数,一个单位的基础倍数。例如 单位是年，xx 年1 次，", required = true)
    @NotNull(message = "单位基础倍数")
    private Integer multiple;

    @ApiModelProperty(value = "次数", required = false)
    private Integer frequency = 1;

}
