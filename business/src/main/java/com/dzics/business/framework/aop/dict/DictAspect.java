package com.dzics.business.framework.aop.dict;


import com.alibaba.fastjson.JSONObject;
import com.dzics.common.aop.Dict;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.SysDictItemService;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Description: 字典aop类
 */
@Aspect
@Component
@Slf4j
public class DictAspect {
    private static String DICT_TEXT_SUFFIX = "_dictText";

    @Autowired
    private SysDictItemService dictItemService;

    // 定义切点Pointcut 拦截所有对服务器的请求
//    @Pointcut("execution( * com.dzics.business.controller.*.*.*.*(..))")
    public void excudeService() {
    }

    /**
     * 这是触发 excudeService 的时候会执行的，在环绕通知中目标对象方法被调用后的结果进行再处理
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
//    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        //这是定义开始事件
//        long time1 = System.currentTimeMillis();
        //这是方法并获取返回结果
        Object result = pjp.proceed();
        //这是获取到 结束时间
//        long time2 = System.currentTimeMillis();
//        log.debug("获取JSON数据 耗时：" + (time2 - time1) + "ms");
        //解析开始时间
//        long start = System.currentTimeMillis();
        //开始解析（翻译字段内部的值凡是打了 @Dict 这玩意的都会被翻译）
        this.parseDictText(result);
        //解析结束时间
//        long end = System.currentTimeMillis();
//        log.debug("解析注入JSON数据  耗时" + (end - start) + "ms");
        return result;
    }

    /**
     * 本方法针对返回对象为Result 的PageUtils的分页列表数据进行动态字典注入
     * 字典注入实现 通过对实体类添加注解@dict 来标识需要的字典内容,字典分为单字典dataSource即可
     * 示例为Student
     * 字段为stu_sex 添加了注解@Dict(dicDataSource = "stu_sex") 会在字典服务立马查出来对应的text 然后在请求list的时候将这个字典text，已字段名称加_dictText形式返回到前端
     * 例输入当前返回值的就会多出一个stu_sex_dictText字段
     * {
     * stu_sex:1,
     * stu_sex_dictText:"男"
     * }
     *
     * @param result
     */
    private void parseDictText(Object result) {
        if (result instanceof Result) {
            Result pageUtils = (Result) result;
            if (pageUtils.getData() == null) {
                return;
            }
            //循环查找出来的数据
            if (pageUtils.getData() instanceof List) {
                List<Object> list = (List<Object>) pageUtils.getData();
                for (Object record : list) {
                    ObjectMapper mapper = new ObjectMapper();
                    String json = "{}";
                    try {
                        //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                        json = mapper.writeValueAsString(record);
                    } catch (JsonProcessingException e) {
                        log.error("json解析失败" + e.getMessage(), e);
                    }
                    JSONObject item = JSONObject.parseObject(json);

                    //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                    //for (Field field : record.getClass().getDeclaredFields()) {
                    for (Field field : ObjConvertUtils.getAllFields(record)) {
                        //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                        if (field.getAnnotation(Dict.class) != null) {
                            //Dict获取注解里面的值
                            String datasource = field.getAnnotation(Dict.class).dicDataSource();
                            String text = field.getAnnotation(Dict.class).dicText();
                            //获取当前带翻译的值
                            String key = String.valueOf(item.get(field.getName()));
                            //翻译字典值对应的txt
                            String textValue = translateDictValue(datasource, key);
                            //  DICT_TEXT_SUFFIX的值为，是默认值：
                            // public static final String DICT_TEXT_SUFFIX = "_dictText";
                            log.debug(" 字典Val : " + textValue);
                            log.debug(" __翻译字典字段__ " + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                            //如果给了文本名
                            if (!StringUtils.isEmpty(text)) {
                                item.put(text, textValue);
                            } else {
                                //走默认策略
                                item.put(field.getName() + DICT_TEXT_SUFFIX, textValue);
                            }

                        }
                        //date类型默认转换string格式化日期
                        if (field.getType().getName().equals("java.util.Date") && field.getAnnotation(JsonFormat.class) == null && item.get(field.getName()) != null) {
                            SimpleDateFormat aDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                        }
                    }
                }
            } else {
                Object record = pageUtils.getData();
                ObjectMapper mapper = new ObjectMapper();
                String json = "{}";
                try {
                    //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                    json = mapper.writeValueAsString(record);
                } catch (JsonProcessingException e) {
                    log.error("json解析失败" + e.getMessage(), e);
                }
                JSONObject item = null;
                try {
                    item = JSONObject.parseObject(json);
                } catch (Exception e) {
                    return;
                }
                //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                //for (Field field : record.getClass().getDeclaredFields()) {
                for (Field field : ObjConvertUtils.getAllFields(record)) {
                    //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                    if (field.getAnnotation(Dict.class) != null) {
                        //Dict获取注解里面的值
                        String datasource = field.getAnnotation(Dict.class).dicDataSource();
                        String text = field.getAnnotation(Dict.class).dicText();
                        //获取当前带翻译的值
                        String key = String.valueOf(item.get(field.getName()));
                        //翻译字典值对应的txt
                        String textValue = translateDictValue(datasource, key);
                        //  DICT_TEXT_SUFFIX的值为，是默认值：
                        // public static final String DICT_TEXT_SUFFIX = "_dictText";
                        log.debug(" 字典Val : " + textValue);
                        log.debug(" __翻译字典字段__ " + field.getName() + DICT_TEXT_SUFFIX + "： " + textValue);
                        //如果给了文本名
                        if (!StringUtils.isEmpty(text)) {
                            item.put(text, textValue);
                        } else {
                            //走默认策略
                            item.put(field.getName() + DICT_TEXT_SUFFIX, textValue);
                        }

                    }
                    //date类型默认转换string格式化日期
                    if (field.getType().getName().equals("java.util.Date") && field.getAnnotation(JsonFormat.class) == null && item.get(field.getName()) != null) {
                        SimpleDateFormat aDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                    }
                }
            }
        }
    }


    /**
     * 翻译字典文本
     *
     * @param datasource
     * @param key
     * @return
     */
    private String translateDictValue(String datasource, String key) {
        //如果key为空直接返回就好了
        if (ObjConvertUtils.isEmpty(key)) {
            return null;
        }
        return dictItemService.getDictTest(datasource, key);

    }


}
