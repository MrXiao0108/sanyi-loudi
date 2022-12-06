package com.dzics.common.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Valid
public class AddWorkShiftVo {

    @ApiModelProperty(value = "产线id")
    private Long productionLineId;

    @ApiModelProperty(value = "班次名称",required = true)
    @NotEmpty(message = "班次名称必填")
    private String workName;

    @ApiModelProperty(value = "班次开始时间",dataType = "java.lang.String",required = true)
    @NotEmpty(message = "班次开始时间必填")
    @DateTimeFormat(pattern = "hh:mm:ss")
    private LocalTime startTime;

    @ApiModelProperty(value = "班次结束时间",dataType = "java.lang.String",required = true)
    @NotEmpty(message = "班次结束时间必填")
    @DateTimeFormat(pattern = "hh:mm:ss")
    private LocalTime endTime;

    @ApiModelProperty(value = "排序码",required = true)
    @NotNull(message = "排序码不能为空")
    private Integer sortNo;

}
