package com.dzics.common.dao;

import com.dzics.common.model.entity.MomReceiveMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 接收来料信息 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-28
 */
@Mapper
public interface MomReceiveMaterialMapper extends BaseMapper<MomReceiveMaterial> {

    List<MomReceiveMaterial> listNoCheck();
}
