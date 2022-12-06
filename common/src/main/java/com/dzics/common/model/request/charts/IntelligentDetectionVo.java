package com.dzics.common.model.request.charts;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class IntelligentDetectionVo {
    @NotNull(message = "请选择产线")
    private Long lineId;
    //产品名(物料类型)
    private String productName;
    //二维码
    private String producBarcode;
    //查询日期
    @NotNull(message = "请填写日期")
    private LocalDate newDate;
}
