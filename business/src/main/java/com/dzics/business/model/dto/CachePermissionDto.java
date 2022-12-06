package com.dzics.business.model.dto;

import com.dzics.common.model.entity.SysPermission;
import com.dzics.common.model.entity.SysRole;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/5.
 */
@Data
public class CachePermissionDto  implements Serializable {
    private List<SysPermission> permissionList;
    private List<SysRole> rolesList;
}
