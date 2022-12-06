package com.dzics.kanban.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dzics.kanban.model.entity.SysMethodGroup;
import com.dzics.kanban.model.request.DelGpOrMdVo;
import com.dzics.kanban.model.request.UpGpOrMdVo;
import com.dzics.kanban.model.response.GroupsMethods;
import com.dzics.kanban.model.response.Result;

import java.util.List;

/**
 * <p>
 * 方法组表 服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-02
 */
public interface SysMethodGroupService extends IService<SysMethodGroup> {
    //查询下拉框
    List<GroupsMethods>getGroup();

    //查询列表
    List<GroupsMethods> getGroupsWithConfig(String sub);

    //新增
    Result addGroupOrMethod(String sub, UpGpOrMdVo addGpOrMdVo);

    //删除
    Result delGroupOrMethod(String sub, DelGpOrMdVo delGpOrMdVo);

    //编辑接口（方法）
    Result upInterfaceByGroup(String sub, UpGpOrMdVo upGpOrMdVo);
}
