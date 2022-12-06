package com.dzics.kanban.auth;

import com.alibaba.fastjson.JSONObject;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.model.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Slf4j
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * Handles an access denied failure.
     *
     * @param request               that resulted in an <code>AccessDeniedException</code>
     * @param response              so that the user agent can be advised of the failure
     * @param accessDeniedException that caused the invocation
     * @throws IOException      in the event of an IOException
     * @throws ServletException in the event of a ServletException
     */
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("权限异常 url {},errorMsg:{}", request.getRequestURI(), accessDeniedException.getMessage());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        Result result = new Result(CustomExceptionType.AUTHEN_TICATIIN_FAILURE);
        writer.write(JSONObject.toJSONString(result));
        writer.flush();
        writer.close();
    }
}
