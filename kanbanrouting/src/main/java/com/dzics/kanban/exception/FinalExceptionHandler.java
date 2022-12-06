package com.dzics.kanban.exception;

import com.dzics.kanban.model.response.RespMessage;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ZhangChengJun
 * Date 2021/4/22.
 * @since
 */
@RestController
public class FinalExceptionHandler implements ErrorController {
    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping(value = "/error")
    public RespMessage error(HttpServletResponse resp, HttpServletRequest req) {
        RespMessage respMessage = new RespMessage();
        respMessage.setCode(0);
        respMessage.setMessage("本次访问无效☹☹");
        return respMessage;
    }
}
