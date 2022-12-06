package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysDepart;

import java.util.List;

/**
 * <p>
 * 站点公司表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysDepartService extends IService<SysDepart> {


    SysDepart getByParentId(int i);

    /**
     * @return 排除大正站点 所有站点
     */
    List<SysDepart> listNotDz();

}
