package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.MomMaterialWarehouse;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 物料仓库 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-12
 */
@Mapper
public interface MomMaterialWarehouseMapper extends BaseMapper<MomMaterialWarehouse> {
    default MomMaterialWarehouse getOrderAndMaterial(String order_no, String lineNo, String materialNo) {
        QueryWrapper<MomMaterialWarehouse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", order_no);
        queryWrapper.eq("line_no", lineNo);
        queryWrapper.eq("material_no", materialNo);
        MomMaterialWarehouse warehouse = selectOne(queryWrapper);
        return warehouse;
    }
}
