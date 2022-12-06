package com.dzics.kanban.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.kanban.model.entity.SysMethodGroupConfiguration;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 方法和方法组关系表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-02
 */
@Mapper
public interface SysMethodGroupConfigurationMapper extends BaseMapper<SysMethodGroupConfiguration> {

}
