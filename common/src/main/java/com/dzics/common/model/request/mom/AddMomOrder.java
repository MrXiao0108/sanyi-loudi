package com.dzics.common.model.request.mom;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * mom 在 dzdc 新增订单
 */
@Data
public class AddMomOrder {
    @ApiModelProperty(value = "大正产线ID", required = true)
    @NotNull(message = "请填写全部表单参数")
    private String lineId;

    /**
     *自动生产  订单号
     */
    @JsonIgnore
    private String wipOrderNo;

    /**
     * 订单类型  1：正常订单；2：返工返修订单
     */
    @ApiModelProperty(value = "订单类型  1：正常订单；2：返工返修订单", required = true)
    @NotNull(message = "请填写全部表单参数")
    private String wipOrderType;

    @ApiModelProperty(value = "大正产品id", required = true)
    @NotNull(message = "请填写全部表单参数")
    private String productId;


    @ApiModelProperty(value = "组件物料集合", required = true)
    @NotNull(message = "请填写全部表单参数")
    List<MaterialAddParms> materialAddParms;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "计划开始时间 yyyy-MM-dd HH:mm:ss", required = true)
    @NotNull(message = "请填写全部表单参数")
    private Date scheduledStartDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "计划结束时间  yyyy-MM-dd HH:mm:ss", required = true)
    @NotNull(message = "请填写全部表单参数")
    private Date scheduledCompleteDate;


    /**
     * 订单需要制作工件数量
     */
    @ApiModelProperty(value = "订单需要制作工件数量", required = true)
    @Min(0)
    private Integer quantity;

    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称", required = true)
    private String workName;
}

