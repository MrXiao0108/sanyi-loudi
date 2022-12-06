package com.dzics.sanymom.config.auth;

import com.alibaba.fastjson.JSON;
import com.dzics.common.dao.SysDictItemMapper;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.model.entity.DzProductionLine;
import com.dzics.common.model.entity.MomUser;
import com.dzics.common.model.entity.SysDictItem;
import com.dzics.common.model.response.Result;
import com.dzics.common.service.MomUserService;
import com.dzics.common.util.RedisKey;
import com.dzics.sanymom.service.CachingApi;
import com.dzics.sanymom.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@SuppressWarnings("ALL")
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisUtil redisUtil;

    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

    @Autowired
    private AntPathMatcher antPathMatcher;
    @Autowired
    private CachingApi cachingApi;
    @Autowired
    private MomUserService momUserService;
    @Value("${order.code}")
    private String orderCodeSys;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        try {
            boolean match = antPathMatcher.match("/api/mom/user/**", requestURI);
            if (match) {
                int i = requestURI.hashCode();
                Object hz = redisUtil.get(RedisKey.SANY_MOM_USER_FREQUENCY + i);
                if (hz != null) {
//                    throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR, CustomResponseCode.ERR01);
                }
                redisUtil.set(RedisKey.SANY_MOM_USER_FREQUENCY + i, 1, 2);
                DzProductionLine line = cachingApi.getOrderIdAndLineId();
                String orderId = line.getOrderNo();
                String lineId = line.getLineNo();
                MomUser loginOk = momUserService.getLineIsLogin(orderId, lineId, orderCodeSys);
                if (loginOk == null) {
                    throw new CustomException(CustomExceptionType.AUTHEN_TOKEN_REF_IS_ERROR);
                }
            }
        } catch (CustomException ex) {
            log.error("登录校验失败：{}", ex.getMessage(), ex);
            responseMsg(response, ex);
            return;
        } catch (Throwable ex) {
            log.error("登录校验异常：{}", ex.getMessage(), ex);
            responseMsg(response, new CustomException(CustomExceptionType.SYSTEM_ERROR));
            return;
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
