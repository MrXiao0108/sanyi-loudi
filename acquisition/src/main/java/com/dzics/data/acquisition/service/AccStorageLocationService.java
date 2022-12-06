package com.dzics.data.acquisition.service;

import com.dzics.common.model.custom.MachiningNumTotal;
import com.dzics.common.model.custom.RabbitmqMessage;
import com.dzics.common.model.entity.DzEquipment;
import com.dzics.common.model.entity.SysRealTimeLogs;
import com.dzics.common.model.response.JCEquimentBase;
import com.dzics.common.model.response.Result;

import java.time.LocalDate;
import java.util.List;

/**
 * 传送带处理接口类
 *
 * @author ZhangChengJun
 * Date 2021/3/20.
 * @since
 */
public interface AccStorageLocationService {

    Result getEquimentStateX(String lineNo, String orderNum);


    /**
     * 设备日产总产
     *
     * @param now     当前日期
     * @param collect 设备id
     * @return
     */
    List<MachiningNumTotal> machiningNumTotals(LocalDate now, List<String> collect);


    List<RabbitmqMessage> createRealTimeLogsDevice(DzEquipment dzEquipment);

    /**
     * 生成udp 回复日志 ，确认收到 来料信号 上次下发回复
     *
     * @param msg
     * @return
     */
    SysRealTimeLogs getDzRealTimelogs( String[] msg);

    /**
     * 机器人回复校验结果，日志保存
     * @param split
     * @return
     */
    SysRealTimeLogs getDzRealTimeRoblogs(String[] split);
}
