package com.dzics.business.service;
import com.dzics.common.model.request.SelectEquipmentStateVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.util.PageLimit;

/**
 * 设备运行状态记录表
 */
public interface BusinessEquipmentStateLogService{
    Result list(String sub, PageLimit pageLimit, SelectEquipmentStateVo selectEquipmentStateVo);

    /**
     * 根据ids删除设备运行日志
     * @param ids
     * @return
     */
    void delEquimentLog(Integer ids);


}
