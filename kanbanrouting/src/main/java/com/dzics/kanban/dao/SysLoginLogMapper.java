package com.dzics.kanban.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.kanban.model.entity.SysLoginLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 登陆日志 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-06
 */
@Mapper
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

}
