package com.dzics.business.exception;

import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.CustomWarnException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.UnexpectedTypeException;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleBindException(MethodArgumentNotValidException ex, HttpServletRequest req) {
        log.error("传递参数异常 url {},异常信息:{}", req.getRequestURI(), ex.getBindingResult().getFieldError().getDefaultMessage());
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, ex.getBindingResult().getFieldError().getDefaultMessage());
    }


    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleBindException(BindException ex, HttpServletRequest req) {
        log.error("传递参数异常 url {},异常信息:{}", req.getRequestURI(), ex.getBindingResult().getFieldError().getDefaultMessage());
//        String collect = ex.getBindingResult().getFieldErrors().stream().map(err -> err.getDefaultMessage()+"|").collect(Collectors.joining());
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, ex.getBindingResult().getFieldError().getDefaultMessage());
    }


    @ExceptionHandler({UnexpectedTypeException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result unexpectedTypeException(UnexpectedTypeException ex, HttpServletRequest req) {
        log.error("传递参数异常 url {},异常信息:{}", req.getRequestURI(), ex.getMessage());
        return new Result(CustomExceptionType.TOKEN_PERRMITRE_ERROR, ex.getMessage());
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result customerException(CustomException e, HttpServletRequest req) {
        log.error("自定义错误异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage());
        return new Result(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result exception(Exception e, HttpServletRequest req) {
        log.error("未知异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage(),e);
        return new Result(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR0);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result httpMessageNotReadableException(HttpMessageNotReadableException e, HttpServletRequest req) {
        log.error("JSON 参数错误 url {},异常信息:{}", req.getRequestURI(), e.getMessage());
        return new Result(CustomExceptionType.SYSTEM_ERROR, CustomResponseCode.ERR12);
    }


    @ExceptionHandler(CustomWarnException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result customerException(CustomWarnException e, HttpServletRequest req) {
        log.warn("警告异常 url {},异常信息:{}", req.getRequestURI(), e.getMessage());
        return new Result(e.getCode(), e.getMessage());
    }
}
