package com.dzics.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.business.service.BusinessDictItemService;
import com.dzics.common.dao.SysDictItemMapper;
import com.dzics.common.dao.SysDictMapper;
import com.dzics.common.enums.Message;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.SysDict;
import com.dzics.common.model.entity.SysDictItem;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.model.request.DictItemVo;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.PageLimit;
import com.dzics.common.util.UnderlineTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
@Slf4j
@Service
public class BusinessDictItemServiceImpl implements BusinessDictItemService {
    @Autowired
    SysDictItemMapper sysDictItemMapper;
    @Autowired
    SysDictMapper sysDictMapper;
    @Autowired
    SysUserServiceDao sysUserServiceDao;


    @Override
    public Result addDictItem(String sub, DictItemVo dictItemVo) {
        //字典类型是存在判断
        SysDict sysDict = sysDictMapper.selectById(dictItemVo.getDictId());
        if(sysDict==null){
            log.error("字典类型id不存在:{}",dictItemVo.getDictId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_13);
        }
        List<SysDictItem> itemText = sysDictItemMapper.selectList(new QueryWrapper<SysDictItem>().eq("dict_id",dictItemVo.getDictId()).eq("item_text", dictItemVo.getItemText()));
        if(itemText.size()>0){
            log.error("字典项文本已存在:{}",dictItemVo.getItemText());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, Message.ERR_14);
        }
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        SysDictItem sysDictItem=new SysDictItem();
        BeanUtils.copyProperties(dictItemVo,sysDictItem);
        sysDictItem.setDictCode(sysDict.getDictCode());
        sysDictItem.setCreateBy(byUserName.getRealname());
        sysDictItemMapper.insert(sysDictItem);
        return new Result(CustomExceptionType.OK,sysDictItem);
    }

    @Override
    public Result delDictItem(String sub, Integer id) {
        int i = sysDictItemMapper.deleteById(id);
        if(i>0){

            return new Result(CustomExceptionType.OK,Message.OK_2);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_6);
    }

    @Override
    public Result updateDictItem(String sub, DictItemVo dictItemVo) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if(dictItemVo.getId()==null){
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_5);
        }
        SysDictItem sysDictItem = sysDictItemMapper.selectById(dictItemVo.getId());
        if(sysDictItem==null){
            log.error("数据字典值id不存在:{}",dictItemVo.getId());
            return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_6);
        }
        sysDictItem.setItemValue(dictItemVo.getItemValue());
        sysDictItem.setItemText(dictItemVo.getItemText());
        sysDictItem.setDescription(dictItemVo.getDescription());
        sysDictItem.setSortOrder(dictItemVo.getSortOrder());
        sysDictItem.setUpdateBy(byUserName.getRealname());
        int i = sysDictItemMapper.updateById(sysDictItem);
        if(i>0){
            return new Result(CustomExceptionType.OK,sysDictItem);
        }
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR,Message.ERR_3);

    }

    @Override
    public Result<SysDictItem> listDictItem(PageLimit pageLimit, Integer dictId) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>().eq("dict_id", dictId);
        if(!StringUtils.isEmpty(pageLimit.getType())){
            if("DESC".equals(pageLimit.getType())){
                wrapper.orderByDesc(UnderlineTool.humpToLine(pageLimit.getField()));
            } else if("ASC".equals(pageLimit.getType())){
                wrapper.orderByAsc(UnderlineTool.humpToLine(pageLimit.getField()));
            }
        }
        List<SysDictItem> dictItems = sysDictItemMapper.selectList(wrapper);
        return new Result(CustomExceptionType.OK,dictItems);
    }

    @Override
    public Result<SysDictItem> getDictItem(String dictCode) {
        QueryWrapper<SysDictItem> wrapper = new QueryWrapper<SysDictItem>().eq("dict_code", dictCode);
        wrapper.orderByAsc("sort_order");
        List<SysDictItem> dictItems = sysDictItemMapper.selectList(wrapper);
        return new Result(CustomExceptionType.OK,dictItems);
    }

    @Override
    public Result<SysDictItem> getItemListByCode(String dictCode) {
        List<SysDictItem> dictItems = sysDictItemMapper.selectList(new QueryWrapper<SysDictItem>().eq("dict_code", dictCode));
        return new Result(CustomExceptionType.OK,dictItems);
    }
}
