package com.dzics.common.dao;

import com.dzics.common.model.entity.MomOrderPath;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 订单工序组工序组 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Repository
@Mapper
public interface MomOrderPathMapper extends BaseMapper<MomOrderPath> {

}
