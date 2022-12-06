package com.dzics.data.acquisition.model.iot;

import lombok.Data;

import java.util.Map;

@Data
public class IotCmdModel {
    private String deviceType;
    private String lineNo;
    private String orderCode;
    private String deviceCode;
    private Long deviceId;
    private Map<String, String> mapCmd;
}
