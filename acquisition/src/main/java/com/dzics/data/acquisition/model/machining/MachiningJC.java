package com.dzics.data.acquisition.model.machining;

import com.dzics.common.model.custom.MachiningMessageStatus;
import com.dzics.common.model.custom.MachiningNumTotal;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 机床设备信息
 *
 * @author ZhangChengJun
 * Date 2021/3/11.
 * @since
 */
@Data
public class MachiningJC implements Serializable {

    /**
     * 数据信息
     */
    private List<MachiningNumTotal> machiningNumTotal;

    /**
     * 状态信息
     */
    private List<MachiningMessageStatus> machiningMessageStatus;

}
