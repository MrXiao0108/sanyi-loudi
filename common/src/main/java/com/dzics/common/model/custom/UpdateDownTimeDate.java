package com.dzics.common.model.custom;

import lombok.Data;

import java.util.Date;

/**
 * 上次停机时间id
 *
 * @author ZhangChengJun
 * Date 2021/5/12.
 * @since
 */
@Data
public class UpdateDownTimeDate {
    private Long id;
    private Date upStopTime;
}
