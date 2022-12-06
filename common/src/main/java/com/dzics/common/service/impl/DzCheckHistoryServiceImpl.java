package com.dzics.common.service.impl;

import com.dzics.common.model.entity.DzCheckHistory;
import com.dzics.common.dao.DzCheckHistoryMapper;
import com.dzics.common.model.request.devicecheck.GetDeviceCheckVo;
import com.dzics.common.model.response.devicecheck.GetDeviceCheckDo;
import com.dzics.common.service.DzCheckHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 设备巡检记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-09-28
 */
@Service
public class DzCheckHistoryServiceImpl extends ServiceImpl<DzCheckHistoryMapper, DzCheckHistory> implements DzCheckHistoryService {

    @Resource
    DzCheckHistoryMapper dzCheckHistoryMapper;
    @Override
    public List<GetDeviceCheckDo> getList(GetDeviceCheckVo getDeviceCheckVo) {
        return dzCheckHistoryMapper.getList(getDeviceCheckVo);
    }
}
