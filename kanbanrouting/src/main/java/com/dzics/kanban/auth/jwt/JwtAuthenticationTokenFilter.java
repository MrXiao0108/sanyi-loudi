package com.dzics.kanban.auth.jwt;

import com.alibaba.fastjson.JSON;
import com.dzics.kanban.exception.CustomException;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.service.impl.MyUserDetailsServiceImpl;
import com.dzics.kanban.util.RedisKey;
import com.dzics.kanban.util.RedisUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@SuppressWarnings("ALL")
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    RedisUtil redisUtil;
    @Resource
    JwtTokenUtil jwtTokenUtil;

    @Resource
    MyUserDetailsServiceImpl myUserDetailsServiceImpl;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String method = request.getMethod();
        if (!method.equals("GET")) {
            String systemRunConfig = myUserDetailsServiceImpl.getSystemRunConfig();
            if (!systemRunConfig.equals("dev")) {
                responseMsg(response, new CustomException(CustomExceptionType.SYS_TEM_DEV_PRO,CustomResponseCode.ERR38.getChinese()));
                return;
            }
        }

        String jwtToken = request.getHeader(jwtTokenUtil.getHeader());
        if (!StringUtils.isEmpty(jwtToken)) {
            String sub = request.getHeader("sub");
            if (StringUtils.isEmpty(sub)) {
                logger.warn("sub不存在header中");
                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "参数异常"));
                return;
            }
            String username = null;
            try {
                username = jwtTokenUtil.getUserNameFromToken(jwtToken, sub);
            } catch (ExpiredJwtException e) {
                logger.warn("token已过期请重新获取token" + e.getMessage());
                responseMsg(response, new CustomException(CustomExceptionType.AUTHEN_TOKEN_IS_ERROR, CustomExceptionType.AUTHEN_TOKEN_IS_ERROR.getTypeDesc()));
                return;
            } catch (UnsupportedJwtException e) {
                logger.warn("token签名错误" + e.getMessage());
                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token签名错误"));
                return;
            } catch (MalformedJwtException e) {
                logger.warn("token签名错误" + e.getMessage());
                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token签名错误"));
                return;
            } catch (SignatureException e) {
                List<String> list = redisUtil.lGet(RedisKey.LEASE_CAR_TOKEN_HISTORY + sub, 0, -1);
                if (!list.isEmpty()) {
                    boolean b = list.stream().anyMatch(token -> token.equals(jwtToken));
                    if (b) {
                        username = sub;
                    } else {
                        logger.warn("token验证错误" + e.getMessage());
                        responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token验证错误"));
                        return;
                    }
                } else {
                    logger.warn("token验证错误" + e.getMessage());
                    responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token验证错误"));
                    return;
                }
            } catch (IllegalArgumentException e) {
                logger.warn("token非法参数" + e.getMessage());
                responseMsg(response, new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR, "token非法参数"));
                return;
            } catch (CustomException e) {
                logger.warn("token非法参数" + e.getMessage());
                responseMsg(response, new CustomException(e.getCode(), e.getMessage()));
                return;
            } catch (Exception e) {
                logger.warn("未知异常", e);
                responseMsg(response, new CustomException(CustomExceptionType.SYSTEM_ERROR, CustomExceptionType.SYSTEM_ERROR.getTypeDesc()));
                return;
            }
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsServiceImpl.loadUserByUsername(username);
                if (!userDetails.isAccountNonLocked()) {
                    logger.warn(userDetails.getUsername() + ":" + CustomExceptionType.USER_IS_LOCK.getTypeDesc());
                    responseMsg(response, new CustomException(CustomExceptionType.USER_IS_LOCK, CustomExceptionType.USER_IS_LOCK.getTypeDesc()));
                    return;
                }
                if (jwtTokenUtil.validateToken(jwtToken, userDetails, sub, username)) {
                    //给使用该JWT令牌的用户进行授权
                    UsernamePasswordAuthenticationToken authenticationToken
                            = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } else {
                responseMsg(response, new CustomException(CustomExceptionType.AUTHEN_TOKEN_IS_ERROR, CustomExceptionType.AUTHEN_TOKEN_IS_ERROR.getTypeDesc()));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void responseMsg(HttpServletResponse response, CustomException e) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter writer = response.getWriter();
        Result error = Result.error(e);
        writer.write(JSON.toJSONString(error));
        writer.flush();
        writer.close();
    }
}
