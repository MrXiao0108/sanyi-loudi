package com.dzics.common.model.request.toolinfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class PutToolInfoVo {

    @ApiModelProperty("刀具组id")
    private Long toolGroupsId;

    @ApiModelProperty("刀具编号")
    List<Integer> toolInfoList;
}
