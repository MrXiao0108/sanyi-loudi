package com.dzics.common.model.mom.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 关重件信息 对象
 */
@Data
public class KeyAccessoryModel{

    //关重件物料号 必填 如果是关重件，扫描上来，非关重件传空值
    @JsonProperty(value = "MaterialNo")
    private String MaterialNo;
    //关重件序列号 必填
    @JsonProperty(value = "MaterialSerialNo")
    private String MaterialSerialNo;
}
