package com.dzics.kanban.service;

import com.dzics.kanban.model.entity.SysInterfaceGroup;
import com.dzics.kanban.model.entity.SysInterfaceMethod;
import com.dzics.kanban.model.request.kb.AddInterfaceGroupConfiguration;
import com.dzics.kanban.model.request.kb.DelInterfaceGroup;
import com.dzics.kanban.model.request.kb.DelInterfaceMethod;
import com.dzics.kanban.model.request.kb.GetGroupConfig;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.util.PageLimit;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 接口方法配置
 *
 * @author ZhangChengJun
 * Date 2021/4/27.
 * @since
 */
public interface InterfaceMethod {
    /**
     * 获取方法接口方法列表
     *
     * @param sub
     * @param pageLimit
     * @return
     */
    Result getInterfaceMethod(String sub, PageLimit pageLimit);

    /**
     * 新增接口
     *
     * @param sub
     * @param interfaceMethod
     * @return
     */
    Result addInterfaceMethod(String sub, SysInterfaceMethod interfaceMethod);

    /**
     * 编辑接口信息
     *
     * @param sub
     * @param sysInterfaceMethod
     * @return
     */
    @CacheEvict(cacheNames = {"interfaceMethod.getMethodGroup"},allEntries = true)
    Result editInterfaceMethod(String sub, SysInterfaceMethod sysInterfaceMethod);

    /**
     * 组列表
     *
     * @param sub
     * @param pageLimit
     * @return
     */
    Result getGroup(String sub, PageLimit pageLimit);

    /**
     * 新增组
     *
     * @param sub
     * @param interfaceGroup
     * @return
     */
    Result addGroup(String sub, SysInterfaceGroup interfaceGroup);

    /**
     * 编辑组
     *
     * @param sub
     * @param interfaceGroup
     * @return
     */
    @CacheEvict(cacheNames = {"interfaceMethod.getMethodGroup"},allEntries = true)
    Result editGroup(String sub, SysInterfaceGroup interfaceGroup);

    /**
     * 设置接口组
     *
     * @param sub
     * @param groupConfiguration
     * @return
     */
    @CacheEvict(cacheNames = {"interfaceMethod.getMethodGroup"},allEntries = true)
    Result addGroupInerfaceConfig(String sub, AddInterfaceGroupConfiguration groupConfiguration);

    /**
     * 查询组配置接口
     *
     * @param sub
     * @param groupConfiguration
     * @return
     */
    Result getGroupInerfaceConfig(String sub, GetGroupConfig groupConfiguration);

    /**
     * 根据组名称获取接口列表
     *
     * @param methodGroup 接口组唯一编码
     * @return
     */
    @Cacheable(cacheNames = {"interfaceMethod.getMethodGroup"}, key = "#methodGroup",unless = "#result == null")
    Result getMethodGroup(String methodGroup);

    /**
     * 根据接口id删除
     * @param sub
     * @param delInterfaceMethod
     * @return
     */
    Result delInterfaceMethod(String sub, DelInterfaceMethod delInterfaceMethod);

    /**
     * 删除组接口
     * @param sub
     * @param interfaceGroup
     * @return
     */
    Result delGroup(String sub, DelInterfaceGroup interfaceGroup);
}
