package com.dzics.common.service;

import com.dzics.common.model.entity.DzCheckHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.devicecheck.GetDeviceCheckDo;

import java.util.List;

/**
 * <p>
 * 设备巡检记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzCheckHistoryService extends IService<DzCheckHistory> {

    List<GetDeviceCheckDo> getList(GetDeviceCheckVo getDeviceCheckVo);
}
