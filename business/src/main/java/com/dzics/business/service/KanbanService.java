package com.dzics.business.service;

import com.dzics.business.model.response.MenusInfo;
import com.dzics.business.model.vo.rolemenu.AddPermission;
import com.dzics.business.model.vo.rolemenu.SelKbRouting;
import com.dzics.business.model.vo.rolemenu.UpdatePermission;
import com.dzics.common.model.entity.SysKanbanRouting;
import com.dzics.common.model.response.Result;

/**
 * 看板相关路由接口信息
 *
 * @author ZhangChengJun
 * Date 2021/4/28.
 * @since
 */
public interface KanbanService {
    /**
     * 采单列表
     * @param sub
     * @return
     */
    Result<MenusInfo> selMenuPermission(String sub);

    Result addPermission(AddPermission addPermission, String sub);

    Result<MenusInfo> selMenuPermissionId(Long id, String sub);

    Result updatePermission(UpdatePermission updatePermission, String sub);

    Result delPermission(Long id, String sub);

    /**
     * 看板路由详情
     * @param kbRouting
     * @param sub
     * @return
     */
    Result selRoutingDetails(SelKbRouting kbRouting, String sub);

    Result<SysKanbanRouting> selRouting(String sub);

    /**
     * 路由节点
     * @param kbRouting
     * @param sub
     * @return
     */
    Result<SysKanbanRouting> selMenuRouting(SelKbRouting kbRouting, String sub);
}
