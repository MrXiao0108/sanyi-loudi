package com.dzics.business.model.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * socket address
 *
 * @author ZhangChengJun
 * Date 2021/3/4.
 */
@Data
public class GetSocketAddressPram {
    @NotNull(message = "产线id必传")
    private String id;
    @NotNull(message = "产线序号必传")
    private String lineNo;
    @NotNull(message = "设备序号必传")
    private String equipmentNo;
    @NotNull(message = "设备id必传")
    private String equipmentId;
}
