package com.dzics.sanymom.exception;

import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.CustomWarnException;
import com.dzics.common.exception.RobRequestException;
import com.dzics.common.model.response.Result;
import com.dzics.sanymom.model.ResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.ResourceAccessException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto handleBindException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.error("传递参数异常 url {},异常信息:{}", req.getRequestURI(), Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage(),ex);
        ResultDto error = ResultDto.error();
        error.setMsg(ex.getBindingResult().getFieldError().getDefaultMessage());
        return error;
    }


    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto handleBindException(BindException ex, HttpServletRequest req) {
        log.error("传递参数异常 url {},异常信息:{}", req.getRequestURI(), Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage(),ex);
        return ResultDto.error();
    }


    @ExceptionHandler({UnexpectedTypeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto unexpectedTypeException(UnexpectedTypeException ex, HttpServletRequest req) {
        log.error("传递参数异常 url {},异常信息:{}", req.getRequestURI(), ex.getMessage(),ex);
        return ResultDto.error();
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto customerException(CustomException e, HttpServletRequest req) {
        log.error("自定义错误异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage(),e);
        ResultDto error = ResultDto.error();
        error.setMsg(e.getMessage());
        return error;
    }

    @ExceptionHandler(CustomMomException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto customerException(CustomMomException e, HttpServletRequest req) {
        log.error("请求MOM异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage(),e);
        return ResultDto.error(e);
    }

    @ExceptionHandler({RobRequestException.class, ResourceAccessException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result robRequestException(Throwable e, HttpServletRequest req) {
        Map<String, String[]> parameterMap = req.getParameterMap();
        log.error("请求参数：{} ,请求操作异常 url {},异常信息:{}", parameterMap, req.getRequestURI(), e.getMessage(), e);
        return Result.ok(e.getMessage());
    }


    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto exception(Throwable e, HttpServletRequest req) {
        log.error("未知异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage(),e);
        return ResultDto.error();
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto httpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest req) {
        log.error("JSON 参数错误 url {},异常信息:{}", req.getRequestURI(), e.getMessage(),e);
        return ResultDto.error();
    }


    @ExceptionHandler(CustomWarnException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultDto customerException(CustomWarnException e, HttpServletRequest req) {
        log.warn("警告异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage(),e);
        return ResultDto.error();
    }
}
