package com.dzics.mqtt.task;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class MapStaticDeviceBase implements Serializable {
    /**
     * 设备本地缓存的基础数据
     */
    public static Map<Long, List<Map<String, Object>>> jc = new HashMap<>();
}
