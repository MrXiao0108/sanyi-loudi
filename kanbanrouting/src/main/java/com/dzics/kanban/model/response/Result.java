package com.dzics.kanban.model.response;

import com.dzics.kanban.exception.CustomException;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/5.
 */
@Data
@ApiModel(value = "通用返回信息", description = "通用数据返回信息")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 状态码
     */
    @ApiModelProperty(value = "状态码 " +
            "0 正常 " +
            "404资源不存在" +
            "407参数异常" +
            "401无权限 " +
            "500系统异常")
    private Integer code;
    /**
     * 提示信息
     */
    @ApiModelProperty(value = "提示信息")
    private String msg;

    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Long count = 0L;

    /**
     * 返回数据对象
     */
    @ApiModelProperty(value = "常用返回数据对象")
    private T data;

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result() {

    }


    public Result(CustomExceptionType ok, T data, long total) {
        this.msg = ok.getTypeDesc();
        this.code = ok.getCode();
        this.data = data;
        this.count = total;
    }

    public Result(List selectList, String ok4) {
    }

    /**
     * 请求出现异常时的响应数据封装
     *
     * @param e
     * @return
     */
    public static Result error(CustomException e) {
        Result resultBean = new Result();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getMessage());
        return resultBean;
    }

    public static Result error(CustomExceptionType e, CustomResponseCode responseCode) {
        Result resultBean = new Result();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(responseCode.getChinese());
        return resultBean;
    }

    public static Result error(CustomExceptionType e) {
        Result resultBean = new Result();
        resultBean.setCode(e.getCode());
        resultBean.setMsg(e.getTypeDesc());
        return resultBean;
    }

    public Result(CustomExceptionType type) {
        this.code = type.getCode();
        this.msg = type.getTypeDesc();
    }

    public Result(CustomExceptionType type, String msg) {
        this.code = type.getCode();
        this.msg = msg;
    }


    public static <T> Result<T> ok() {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        return m;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> m = new Result<T>();
        m.setCode(CustomExceptionType.OK.getCode());
        m.setMsg(CustomExceptionType.OK.getTypeDesc());
        m.setData(data);
        return m;
    }


    public Result(CustomExceptionType type, T data) {
        this.code = type.getCode();
        this.msg = type.getTypeDesc();
        this.data = data;
    }
}
