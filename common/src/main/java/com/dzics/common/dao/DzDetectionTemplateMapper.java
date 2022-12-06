package com.dzics.common.dao;

import com.dzics.common.model.entity.DzDetectionTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.request.DzDetectTempVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 产品检测设置默认模板 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Mapper
public interface DzDetectionTemplateMapper extends BaseMapper<DzDetectionTemplate> {

    List<DzDetectTempVo> listDzDetectTempVo();

}
