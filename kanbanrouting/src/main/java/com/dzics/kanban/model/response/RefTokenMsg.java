package com.dzics.kanban.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "刷新token信息")
@Data
public class RefTokenMsg implements Serializable {
    /**
     *access_token 接口调用凭证超时时间，单位（秒）
     */
    @ApiModelProperty(value = "ref_token 接口调用凭证超时时间，单位（秒）")
    private long expires_in;

    @ApiModelProperty(value = "创建时间")
    private long creatTime;
}
