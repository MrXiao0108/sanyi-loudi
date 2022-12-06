package com.dzics.common.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.model.entity.DzWorkReportSort;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-05-30
 */
@Mapper
public interface DzWorkReportSortMapper extends BaseMapper<DzWorkReportSort> {

    default DzWorkReportSort getByOrderId(Long orderId, Long lineId) {
        PageHelper.startPage(1, 1);
        QueryWrapper<DzWorkReportSort> wp = new QueryWrapper<>();
        wp.eq("order_id", orderId);
        wp.eq("line_id", lineId);
        wp.orderByAsc("create_time");
        List<DzWorkReportSort> dzWorkReportSorts = selectList(wp);
        PageInfo<DzWorkReportSort> info = new PageInfo<>(dzWorkReportSorts);
        if (info.getTotal() > 0) {
            return info.getList().get(0);
        }
        return null;
    }

    default void deleteByProTaskId(String proTaskOrderId){
        QueryWrapper<DzWorkReportSort> wp = new QueryWrapper<>();
        wp.eq("pro_task_order_id",proTaskOrderId);
        delete(wp);
    }
}
