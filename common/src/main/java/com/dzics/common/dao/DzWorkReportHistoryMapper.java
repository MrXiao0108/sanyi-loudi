package com.dzics.common.dao;

import com.dzics.common.model.entity.DzWorkReportHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 待报工订单有序的是 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2022-06-02
 */
@Mapper
public interface DzWorkReportHistoryMapper extends BaseMapper<DzWorkReportHistory> {

}
