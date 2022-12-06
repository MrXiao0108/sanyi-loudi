package com.dzics.common.service;

import com.dzics.common.model.entity.DzWorkingFlowBig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 工件制作流程记录 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-20
 */
public interface DzWorkingFlowBigService extends IService<DzWorkingFlowBig> {

    List<DzWorkingFlowBig> getInitFlowBig();

}
