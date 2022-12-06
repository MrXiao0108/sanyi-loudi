package com.dzics.kanban.auth;

import com.alibaba.fastjson.JSON;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.util.IPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author ZhangChengJun
 * Date 2020/5/18.
 */
@Component
@Slf4j
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * Commences an authentication scheme.
     * <p>
     * <code>ExceptionTranslationFilter</code> will populate the <code>HttpSession</code>
     * attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p>
     * Implementations should modify the headers on the <code>ServletResponse</code> as
     * necessary to commence the authentication process.
     *
     * @param request       that resulted in an <code>AuthenticationException</code>
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        String jwtToken = request.getHeader("jwt_token");
        log.warn("请求地址：{},URL：{}，jwtToken：{}，异常信息：{}", IPUtil.getIpAddress(request), request.getRequestURI(),jwtToken, authException.getMessage());
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        Result error = new Result(CustomExceptionType.AUTHEN_TICATIIN_FAILURE, authException.getMessage());
        if (authException instanceof InsufficientAuthenticationException) {
            error = new Result(CustomExceptionType.AUTHEN_TICATIIN_FAILURE);
        }
        writer.write(JSON.toJSONString(error));
        writer.flush();
        writer.close();
    }
}
