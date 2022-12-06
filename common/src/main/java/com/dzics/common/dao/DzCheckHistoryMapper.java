package com.dzics.common.dao;

import com.dzics.common.model.entity.DzCheckHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.devicecheck.GetDeviceCheckDo;

import java.util.List;

/**
 * <p>
 * 设备巡检记录 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
public interface DzCheckHistoryMapper extends BaseMapper<DzCheckHistory> {

    List<GetDeviceCheckDo> getList(GetDeviceCheckVo getDeviceCheckVo);
}
