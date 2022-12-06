package com.dzics.common.model.request.plan;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
public class SelectEquipmentProductionDetailsVo {
    @ApiModelProperty("设备id")
    @NotNull(message = "选择设备")
   private Long equimentId;
    @ApiModelProperty("日期")
    @NotNull(message = "日期不能为空")
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date workDate;
}
