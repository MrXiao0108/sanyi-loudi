package com.dzics.kanban.util;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.dzics.kanban.model.entity.SysKanbanRouting;
import com.dzics.kanban.model.entity.SysPermission;
import com.dzics.kanban.model.response.MetaInfo;
import com.dzics.kanban.model.response.RoutersInfo;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 树形节点方法抽取
 *
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
public class ChildrenUtils {


    public static List<Map<String, Object>> treeSelectMap(List<RoutersInfo> parRoutEnd) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RoutersInfo routersInfo : parRoutEnd) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", routersInfo.getMeta().getTitle());
            map.put("id", routersInfo.getId());
            if (routersInfo.getChildren() != null && !routersInfo.getChildren().isEmpty()) {
                List<Map<String, Object>> o = childrenAdd(routersInfo.getChildren());
                if (o != null && !o.isEmpty()) {
                    map.put("children", o);
                }
            }
            list.add(map);
        }
        return list;
    }

    public static List<Map<String, Object>> childrenAdd(List<RoutersInfo> routersInfos) {
        List<Map<String, Object>> c = new ArrayList<>();
        for (RoutersInfo child : routersInfos) {
            Map<String, Object> map = new HashMap<>();
            map.put("label", child.getMeta().getTitle());
            map.put("id", child.getId());
            if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                List<Map<String, Object>> o = childrenAdd(child.getChildren());
                if (o != null && !o.isEmpty()) {
                    map.put("children", o);
                }

            }
            c.add(map);
        }
        return c;
    }

    public static List<RoutersInfo> getChildRoutersInfo(List<RoutersInfo> parRout, List<SysPermission> permissionsNext) {
        List<RoutersInfo> routersInfos = new ArrayList<>();
        for (RoutersInfo routersInfo : parRout) {
            List<RoutersInfo> childList = getNextRouts(permissionsNext, routersInfo.getId());
            if (CollectionUtils.isNotEmpty(childList)) {
                routersInfo.setChildren(childList);
                getChildRoutersInfo(childList, permissionsNext);
            }
            routersInfos.add(routersInfo);
        }
        return routersInfos;
    }

    public static List<RoutersInfo> getChildRoutersInfoKb(List<RoutersInfo> parRout, List<SysKanbanRouting> permissionsNext) {
        List<RoutersInfo> routersInfos = new ArrayList<>();
        for (RoutersInfo routersInfo : parRout) {
            List<RoutersInfo> childList = getNextRoutsKb(permissionsNext, routersInfo.getId());
            if (CollectionUtils.isNotEmpty(childList)) {
                routersInfo.setChildren(childList);
                getChildRoutersInfoKb(childList, permissionsNext);
            }
            routersInfos.add(routersInfo);
        }
        return routersInfos;
    }

    public static List<RoutersInfo> getNextRouts(List<SysPermission> permissionsNext, Long id) {
        List<RoutersInfo> routersInfos = new ArrayList<>();
        for (SysPermission permission : permissionsNext) {
            if (permission.getParentId().longValue() == id.longValue()) {
                routersInfos.add(permissionRoutersInfo(permission));
            }
        }
        return routersInfos;
    }

    public static List<RoutersInfo> getNextRoutsKb(List<SysKanbanRouting> permissionsNext, Long id) {
        List<RoutersInfo> routersInfos = new ArrayList<>();
        for (SysKanbanRouting permission : permissionsNext) {
            if (permission.getParentId().longValue() == id.longValue()) {
                routersInfos.add(permissionRoutersInfoKb(permission));
            }
        }
        return routersInfos;
    }
    public static List<RoutersInfo> parPermissionNext(List<SysPermission> list, List<SysPermission> permissionsNext) {
//        一级节点数据
        List<RoutersInfo> mapList = new ArrayList<>();
//      子节点数据
        for (SysPermission permission : list) {
            if (permission.getParentId().intValue() == 0) {
                mapList.add(permissionRoutersInfo(permission));
            } else {
                permissionsNext.add(permission);
            }

        }
        return mapList;
    }


    public static List<RoutersInfo> parPermissionNextKb(List<SysKanbanRouting> list, List<SysKanbanRouting> permissionsNext) {
//        一级节点数据
        List<RoutersInfo> mapList = new ArrayList<>();
//      子节点数据
        for (SysKanbanRouting permission : list) {
            if (permission.getParentId().intValue() == 0) {
                mapList.add(permissionRoutersInfoKb(permission));
            } else {
                permissionsNext.add(permission);
            }

        }
        return mapList;
    }

    public static RoutersInfo permissionRoutersInfo(SysPermission permission) {
        RoutersInfo routersInfo = new RoutersInfo();
        routersInfo.setId(permission.getId());
        routersInfo.setPid(permission.getParentId());
        routersInfo.setRedirect(permission.getRedirect());
        routersInfo.setPath(permission.getPath());
        routersInfo.setComponent(permission.getComponent());
        routersInfo.setHidden(permission.getHidden().intValue() == 0 ? false : true);
        routersInfo.setChildren(Lists.newArrayList());
        routersInfo.setMeta(new MetaInfo(false, permission.getIcon(), permission.getTitle()));
        routersInfo.setName(permission.getName());
        routersInfo.setAlwaysShow(permission.getAlwaysShow());
        return routersInfo;
    }


    public static RoutersInfo permissionRoutersInfoKb(SysKanbanRouting permission) {
        RoutersInfo routersInfo = new RoutersInfo();
        routersInfo.setId(permission.getId());
        routersInfo.setPid(permission.getParentId());
        routersInfo.setRedirect(permission.getRedirect());
        routersInfo.setPath(permission.getPath());
        routersInfo.setComponent(permission.getComponent());
        routersInfo.setHidden(permission.getHidden().intValue() == 0 ? false : true);
        routersInfo.setChildren(Lists.newArrayList());
        routersInfo.setMeta(new MetaInfo(false, permission.getIcon(), permission.getTitle()));
        routersInfo.setName(permission.getName());
        routersInfo.setAlwaysShow(permission.getAlwaysShow());
        return routersInfo;
    }
}
