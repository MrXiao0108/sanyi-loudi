package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusinessToolGroupsService;
import com.dzics.common.dao.DzToolCompensationDataMapper;
import com.dzics.common.dao.DzToolGroupsMapper;
import com.dzics.common.dao.DzToolInfoMapper;
import com.dzics.common.dao.SysDepartMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.DzToolCompensationData;
import com.dzics.common.model.entity.DzToolGroups;
import com.dzics.common.model.entity.DzToolInfo;
import com.dzics.common.model.entity.SysDepart;
import com.dzics.common.model.request.toolinfo.AddDzToolGroupVo;
import com.dzics.common.model.request.toolinfo.GetToolInfoDataListVo;
import com.dzics.common.model.request.toolinfo.PutToolGroupsVo;
import com.dzics.common.model.request.toolinfo.PutToolInfoVo;
import com.dzics.common.model.response.GetToolInfoDataListDo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.DzToolInfoService;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BusinessToolGroupsServiceImpl implements BusinessToolGroupsService {

    @Autowired
    DzToolGroupsMapper dzToolGroupsMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    DzToolInfoMapper dzToolInfoMapper;
    @Autowired
    DzToolInfoService dzToolInfoService;
    @Autowired
    DzToolCompensationDataMapper dzToolCompensationDataMapper;
    @Autowired
    SysDepartMapper sysDepartMapper;

    @Override
    public Result<List<DzToolGroups>> getToolGroupsList(String sub, PageLimit pageLimit, String groupNo) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        List<DzToolGroups> list = dzToolGroupsMapper.getToolGroupsList(pageLimit.getField(), pageLimit.getType(), orgCode, groupNo);
        PageInfo<DzToolGroups> pageInfo = new PageInfo<>(list);
        return new Result(CustomExceptionType.OK, pageInfo.getList(), pageInfo.getTotal());
    }

    @Override
    @Transactional
    public Result addToolGroups(AddDzToolGroupVo addDzToolGroupVo) {
        List<DzToolGroups> groupNo = dzToolGroupsMapper.selectList(new QueryWrapper<DzToolGroups>().eq("group_no", addDzToolGroupVo.getGroupNo()));
        if (groupNo.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_100);
        }
        if (addDzToolGroupVo.getToolNoList() == null || addDzToolGroupVo.getToolNoList().size() == 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_101);
        }
        //刀具编号去重判断
        int size = addDzToolGroupVo.getToolNoList().size();
        List<Integer> collect = addDzToolGroupVo.getToolNoList().stream().distinct().collect(Collectors.toList());
        if (collect.size() != size) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
        }
        //刀具编号数据库去重判断
//        List<DzToolInfo> dzToolInfos = dzToolInfoMapper.selectList(new QueryWrapper<DzToolInfo>());
//        if(dzToolInfos.size()>0){
//            List<Integer> toolNoList = addDzToolGroupVo.getToolNoList();
//            for (Integer toolNo:toolNoList) {
//                if(dzToolInfos.contains(toolNo)){
//                    return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.err110+toolNo);
//                }
//            }
//        }

        //添加数据
        SysDepart sysDepart = sysDepartMapper.selectById(addDzToolGroupVo.getId());
        if (sysDepart == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_61);
        }
        DzToolGroups dzToolGroups = new DzToolGroups();
        dzToolGroups.setOrgCode(sysDepart.getOrgCode());
        dzToolGroups.setGroupNo(addDzToolGroupVo.getGroupNo());
        dzToolGroupsMapper.insert(dzToolGroups);
        List<DzToolInfo> toolList = new ArrayList<>();
        for (Integer toolNo : addDzToolGroupVo.getToolNoList()) {
            DzToolInfo dzToolInfo = new DzToolInfo();
            dzToolInfo.setToolGroupsId(dzToolGroups.getToolGroupsId());
            dzToolInfo.setToolNo(toolNo);
            dzToolInfo.setOrgCode(dzToolGroups.getOrgCode());
            toolList.add(dzToolInfo);
        }
        dzToolInfoService.saveBatch(toolList);
        return Result.ok();
    }

    @Override
    public Result delToolGroups(Long toolGroupsId) {
        if (toolGroupsId == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_103);
        }
        DzToolGroups dzToolGroups = dzToolGroupsMapper.selectById(toolGroupsId);
        if (dzToolGroups == null) {
            return Result.ok();
        }
        //判断刀具组是否绑定了设备
        QueryWrapper<DzToolCompensationData> wrapper = new QueryWrapper<>();
        wrapper.eq("group_no", dzToolGroups.getGroupNo());
        List<DzToolCompensationData> list = dzToolCompensationDataMapper.selectList(wrapper);
        if (list.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_102);
        }
        //删除刀具组下面的所有刀具
        dzToolInfoMapper.delete(new QueryWrapper<DzToolInfo>().eq("tool_groups_id", toolGroupsId));
        //删除刀具组
        dzToolGroupsMapper.deleteById(toolGroupsId);
        return Result.ok();
    }

    @Override
    public Result putToolGroups(PutToolGroupsVo putToolGroupsVo) {

        DzToolGroups dzToolGroups = dzToolGroupsMapper.selectById(putToolGroupsVo.getToolGroupsId());
        if (dzToolGroups == null) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_104);
        }
        if (dzToolGroups.getGroupNo().intValue() != putToolGroupsVo.getGroupNo().intValue()) {
            //刀具组编号有变化，判断新的刀具组编号是否存在
            List<DzToolGroups> groupNo = dzToolGroupsMapper.selectList(new QueryWrapper<DzToolGroups>().eq("group_no", putToolGroupsVo.getGroupNo()));
            if (groupNo.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_105);
            } else {
                dzToolGroups.setGroupNo(putToolGroupsVo.getGroupNo());
                dzToolGroupsMapper.updateById(dzToolGroups);
            }
        }
        return Result.ok();
    }

    @Override
    public Result getToolInfoList(Long toolGroupsId, PageLimit pageLimit) {
        QueryWrapper<DzToolInfo> tool_groups_id = new QueryWrapper<DzToolInfo>();
        tool_groups_id.eq("tool_groups_id", toolGroupsId);
        if (!StringUtils.isEmpty(pageLimit.getField())) {
            pageLimit.setField(UnderlineTool.humpToLine(pageLimit.getField()));
            if (!StringUtils.isEmpty(pageLimit.getType())) {
                if (pageLimit.getType().equals("ASC")) {
                    tool_groups_id.orderByAsc(pageLimit.getField());
                } else if (pageLimit.getType().equals("DESC")) {
                    tool_groups_id.orderByDesc(pageLimit.getField());
                }
            }
        }
        List<DzToolInfo> dzToolInfos = dzToolInfoMapper.selectList(tool_groups_id);
        return Result.ok(dzToolInfos);
    }

    @Override
    public Result putToolInfo(PutToolInfoVo putToolGroupsVo) {
        //刀具长度非空判断
        if (putToolGroupsVo.getToolInfoList() == null || putToolGroupsVo.getToolInfoList().size() == 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_101);
        }
        //刀具去重判断
        int size = putToolGroupsVo.getToolInfoList().size();
        List<Integer> collect = putToolGroupsVo.getToolInfoList().stream().distinct().collect(Collectors.toList());
        if (collect.size() != size) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
        }
        DzToolGroups dzToolGroups = dzToolGroupsMapper.selectById(putToolGroupsVo.getToolGroupsId());
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id：{}", putToolGroupsVo.getToolGroupsId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_6);
        }

        //删除旧的刀具
        dzToolInfoMapper.delete(new QueryWrapper<DzToolInfo>().eq("tool_groups_id", putToolGroupsVo.getToolGroupsId()));
        //添加刀具
        List<DzToolInfo> toolList = new ArrayList<>();
        for (Integer toolNo : putToolGroupsVo.getToolInfoList()) {
            DzToolInfo dzToolInfo = new DzToolInfo();
            dzToolInfo.setToolGroupsId(dzToolGroups.getToolGroupsId());
            dzToolInfo.setToolNo(toolNo);
            dzToolInfo.setOrgCode(dzToolGroups.getOrgCode());
            toolList.add(dzToolInfo);
        }
        dzToolInfoService.saveBatch(toolList);
        return Result.ok();
    }

    @Override
    public Result delToolInfo(Long id) {
        DzToolInfo dzToolInfo = dzToolInfoMapper.selectById(id);
        if (dzToolInfo == null) {
            log.error("刀具id不存在，id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        DzToolGroups dzToolGroups = dzToolGroupsMapper.selectById(dzToolInfo.getToolGroupsId());
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id:{}", dzToolInfo.getToolGroupsId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        Long count = dzToolCompensationDataMapper.getToolCompensationDataByToolInfo(dzToolInfo.getToolNo(), dzToolGroups.getGroupNo());
        if (count > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_107);
        } else {
            dzToolInfoMapper.deleteById(id);
            return Result.ok();
        }

    }

    @Override
    public Result putToolInfo(Long id, Integer toolNo) {
        DzToolInfo dzToolInfo = dzToolInfoMapper.selectById(id);
        if (dzToolInfo == null) {
            log.error("刀具id不存在，id:{}", id);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        DzToolGroups dzToolGroups = dzToolGroupsMapper.selectById(dzToolInfo.getToolGroupsId());
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id:{}", dzToolInfo.getToolGroupsId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        if (dzToolInfo.getToolNo().intValue() != toolNo.intValue()) {
            //刀具号发生变化
            //判断新的刀具号是否重复
            QueryWrapper<DzToolInfo> wrapper = new QueryWrapper<DzToolInfo>();
            wrapper.eq("tool_groups_id", dzToolInfo.getToolGroupsId());
            wrapper.eq("tool_no", toolNo);
            List<DzToolInfo> dzToolInfos = dzToolInfoMapper.selectList(wrapper);
            if (dzToolInfos.size() > 0) {
                return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
            }
            //修改绑定设备的数据表  刀具编号
            dzToolCompensationDataMapper.updateByToolNo(dzToolInfo.getToolNo(), dzToolGroups.getGroupNo(), toolNo);
            //修改刀具表  刀具编号
            dzToolInfo.setToolNo(toolNo);
            dzToolInfoMapper.updateById(dzToolInfo);
        }
        return Result.ok();
    }

    @Override
    public Result addToolInfo(Long toolGroupId, Integer toolNo) {
        DzToolGroups dzToolGroups = dzToolGroupsMapper.selectById(toolGroupId);
        if (dzToolGroups == null) {
            log.error("刀具组id不存在，id:{}", toolGroupId);
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_108);
        }
        //判断刀具组下编号是否重复
        QueryWrapper<DzToolInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("tool_groups_id", toolGroupId);
        wrapper.eq("tool_no", toolNo);
        List<DzToolInfo> dzToolInfos = dzToolInfoMapper.selectList(wrapper);
        if (dzToolInfos.size() > 0) {
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_106);
        }
        DzToolInfo dzToolInfo = new DzToolInfo();
        dzToolInfo.setToolGroupsId(toolGroupId);
        dzToolInfo.setToolNo(toolNo);
        dzToolInfo.setOrgCode(dzToolGroups.getOrgCode());
        dzToolInfoMapper.insert(dzToolInfo);
        return Result.ok(dzToolInfo);
    }

    @Override
    public Result getToolGroupsAll(String sub) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        QueryWrapper<DzToolGroups> eq = new QueryWrapper<>();
        if (orgCode != null) {
            eq.eq("org_code", orgCode);
        }
        List<DzToolGroups> list = dzToolGroupsMapper.selectList(eq);
        return Result.ok(list);
    }

    @Override
    public Result<List<GetToolInfoDataListDo>> getToolInfoDataList(String sub, PageLimit pageLimit, GetToolInfoDataListVo getToolInfoDataListVo) {
        String orgCode = sysUserServiceDao.getUserOrgCode(sub);
        getToolInfoDataListVo.setOrgCode(orgCode);
        if (pageLimit.getPage() != -1) {
            PageHelper.startPage(pageLimit.getPage(), pageLimit.getLimit());
        }
        if (!StringUtils.isEmpty(getToolInfoDataListVo.getField())) {
            getToolInfoDataListVo.setField(UnderlineTool.humpToLine(getToolInfoDataListVo.getField()));
        }
        List<GetToolInfoDataListDo> dataList = dzToolCompensationDataMapper.getToolInfoDataList(getToolInfoDataListVo);
        PageInfo<GetToolInfoDataListDo> info = new PageInfo<>(dataList);
        return new Result(CustomExceptionType.OK, info.getList(), info.getTotal());
    }
}
