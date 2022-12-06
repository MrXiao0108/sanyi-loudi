package com.dzics.common.service;

import com.dzics.common.model.entity.SysUserDepart;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
public interface SysUserDepartService extends IService<SysUserDepart> {

    /**
     * @param userId  用户id
     * @param useOrgCode 系统编码
     * @return
     */
    List<SysUserDepart> listByUserIdOrgcodeNeDepartId(Long userId, String useOrgCode,Long departId);

    void removeUserId(Long delUser);
}
