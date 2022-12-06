package com.dzics.common.model.response;

import lombok.Data;

/**
 * 设备名称ID
 *
 * @author ZhangChengJun
 * Date 2021/6/3.
 * @since
 */
@Data
public class DevcieNameId {
    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备ID
     */
    private Long deviceId;
}
