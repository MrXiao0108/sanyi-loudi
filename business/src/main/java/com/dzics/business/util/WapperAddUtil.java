package com.dzics.business.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzics.common.annotation.QueryType;
import com.dzics.common.enums.QueryTypeEnu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 自动添加wapper拼接sql
 *
 * @author ZhangChengJun
 * Date 2021/1/12.
 * @since
 */
@Component
@Slf4j
public class WapperAddUtil {
    /**
     * 获取所有字段
     * 字段值
     * 注解值
     *
     * @param object
     * @throws Exception
     */
    public void addQuery(Object object, QueryWrapper queryWrapper) {
        for (Field field : object.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            TableField annotation = field.getAnnotation(TableField.class);
            QueryType queryType = field.getAnnotation(QueryType.class);
            try {
                log.debug("字段名：" + field.getName() + "->" + field.get(object) + "->sqlFiled->:" + annotation.value());
                if (field.get(object) != null && !field.get(object).equals("")) {
                    if (queryType == null || queryType.value() == QueryTypeEnu.eq) {
                        queryWrapper.eq(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.ne) {
                        queryWrapper.ne(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.gt) {
                        queryWrapper.gt(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.ge) {
                        queryWrapper.ge(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.lt) {
                        queryWrapper.lt(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.le) {
                        queryWrapper.le(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.like) {
                        queryWrapper.like(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.notLike) {
                        queryWrapper.notLike(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.likeLeft) {
                        queryWrapper.likeLeft(annotation.value(), field.get(object));
                    } else if (queryType.value() == QueryTypeEnu.likeRight) {
                        queryWrapper.likeRight(annotation.value(), field.get(object));
                    }else if (queryType.value() == QueryTypeEnu.ge){
                        queryWrapper.ge(annotation.value(), field.get(object));
                    }else if (queryType.value() == QueryTypeEnu.lt){
                        queryWrapper.lt(annotation.value(), field.get(object));
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("拼接sql查询字段错误：{}", e);
            }
        }
    }

    /**
     * 获取指定字段名，值
     * @param object 对象
     * @param name 字段名
     * @throws Exception
     */
   /* public static void getOne (Object object,String name) throws Exception {
        System.out.println("--------获取单个字段：名：值------------");
        Field field = object.getClass().getDeclaredField(name);
        System.out.println(field.get(object));
    }
    public static void main(String[] args) throws Exception {

        SelUser selUser = new SelUser();

        selUser.setUsername("admin");
        selUser.setRealname("1245434112");
        selUser.setStatus(0);

        PageUtil<SelUser> userPageUtil = new PageUtil<>();
        userPageUtil.setPage(10);
        userPageUtil.setLimit(20);
//        userPageUtil.setData(selUser);
        getAll(selUser);
//        getOne(userPageUtil,"address");

    }*/
}
