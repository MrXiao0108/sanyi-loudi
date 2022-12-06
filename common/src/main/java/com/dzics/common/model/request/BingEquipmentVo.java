package com.dzics.common.model.request;

import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;

@Data
public class BingEquipmentVo {
    @NotNull(message = "请选择设备")
    private Long equipmentId;
    @NotNull(message = "请选择产线")
    private Long lineId;
}
