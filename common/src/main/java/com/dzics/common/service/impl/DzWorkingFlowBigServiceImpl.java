package com.dzics.common.service.impl;

import com.dzics.common.model.entity.DzWorkingFlowBig;
import com.dzics.common.dao.DzWorkingFlowBigMapper;
import com.dzics.common.service.DzWorkingFlowBigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 工件制作流程记录 服务实现类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-20
 */
@Service
@Slf4j
public class DzWorkingFlowBigServiceImpl extends ServiceImpl<DzWorkingFlowBigMapper, DzWorkingFlowBig> implements DzWorkingFlowBigService {

    @Override
    public List<DzWorkingFlowBig> getInitFlowBig() {
       return this.baseMapper.getInitFlowBig();
    }
}
