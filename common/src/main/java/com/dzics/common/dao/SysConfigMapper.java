package com.dzics.common.dao;

import com.dzics.common.model.entity.SysConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.RunDataModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统运行模式 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-05-31
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    RunDataModel systemRunModel(@Param("rumModel") String rumModel);

    List<String> getMouthDate(@Param("year") int year, @Param("monthValue") int monthValue);
}
