package com.dzics.common.dao;

import com.dzics.common.model.entity.SysOperationLogging;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

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
@Repository
public interface SysOperationLoggingMapper extends BaseMapper<SysOperationLogging> {

}
