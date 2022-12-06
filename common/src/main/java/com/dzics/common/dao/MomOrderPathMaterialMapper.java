package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomOrderPathMaterial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 工序物料表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-27
 */
@Mapper
@Repository
public interface MomOrderPathMaterialMapper extends BaseMapper<MomOrderPathMaterial> {

    default void delMomOrderId(String momOrderId) {
        delete(new QueryWrapper<MomOrderPathMaterial>().eq("mom_order_id", momOrderId));
    }

    default List<MomOrderPathMaterial> selMomOrderId(String momOrderId) {
        return selectList(new QueryWrapper<MomOrderPathMaterial>().eq("mom_order_id", momOrderId));
    }
}
