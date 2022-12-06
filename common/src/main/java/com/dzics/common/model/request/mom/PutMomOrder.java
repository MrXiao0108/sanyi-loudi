package com.dzics.common.model.request.mom;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class PutMomOrder implements Serializable {

    @ApiModelProperty("Id")
    @NotEmpty(message = "主键不能为空")
    private String proTaskOrderId;
    @ApiModelProperty("110已下达 120进行中 130已完工 140已删除 150强制关闭160暂停")
    private String progressStatus;

    @NotEmpty(message = "产线id不能为空")
    @ApiModelProperty("产线id")
    private String lineId;

    private String transPondKey;
}
