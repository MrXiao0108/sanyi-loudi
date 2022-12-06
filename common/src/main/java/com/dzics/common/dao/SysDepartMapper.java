package com.dzics.common.dao;

import com.dzics.common.model.entity.SysDepart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dzics.common.model.response.SwitchSiteDo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 站点公司表 Mapper 接口
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Mapper
@Repository
public interface SysDepartMapper extends BaseMapper<SysDepart> {

    List<SysDepart> getDepart(String orgCode);

    SysDepart getByCode(@Param("useOrgCode") String useOrgCode);

    List<SwitchSiteDo> listId(@Param("list") List<Long> list);

    List<SwitchSiteDo> listAll();

    SwitchSiteDo getByOrgCode(@Param("orgCode") String orgCode);

    SysDepart selectByLineId(Long lineId);
}
