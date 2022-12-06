package com.dzics.common.service;

import com.dzics.common.model.entity.MomReceiveMaterial;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 接收来料信息 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-28
 */
public interface MomReceiveMaterialService extends IService<MomReceiveMaterial> {

    List<MomReceiveMaterial> listNoCheck(String orderNo, String lineNo);

}
