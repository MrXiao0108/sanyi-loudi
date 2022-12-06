package com.dzics.queueforwarding.exception;

import com.alibaba.fastjson.JSONObject;
import com.dzics.queueforwarding.util.RespMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class WebExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.OK);
    }
    @ExceptionHandler(value = Throwable.class)
    @ResponseBody
    public String handleBindException(Throwable ex) {
        RespMessage respMessage = new RespMessage();
        respMessage.setCode(0);
        respMessage.setMessage("服务正常");
        return JSONObject.toJSONString(respMessage);
    }


}
