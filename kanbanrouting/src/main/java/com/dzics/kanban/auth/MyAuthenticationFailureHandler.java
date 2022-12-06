package com.dzics.kanban.auth;

import com.alibaba.fastjson.JSON;
import com.dzics.kanban.exception.CustomException;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.model.response.Result;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ZhangChengJun
 * Date 2020/5/15.
 * 登录失败返回内容定义
 */

@Component
public class MyAuthenticationFailureHandler implements AuthenticationFailureHandler {
    /**
     * Called when an authentication attempt fails.
     *
     * @param request   the request during which the authentication attempt occurred.
     * @param response  the response.
     * @param exception the exception which was thrown to reject the authentication
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        Result error = Result.error(new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, exception.getMessage()));
        writer.write(JSON.toJSONString(error));
    }
}
