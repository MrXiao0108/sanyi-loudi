package com.dzics.kanban.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.kanban.model.entity.SysOperationLogging;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 操作日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Mapper
public interface SysOperationLoggingMapper extends BaseMapper<SysOperationLogging> {

    List<SysOperationLogging> queryOperLog(@Param("startLimit") int startLimit, @Param("limit") int limit);
}
