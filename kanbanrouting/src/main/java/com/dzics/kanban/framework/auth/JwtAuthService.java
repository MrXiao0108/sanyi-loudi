package com.dzics.kanban.framework.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.dzics.kanban.auth.jwt.JwtTokenUtil;
import com.dzics.kanban.exception.CustomException;
import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.model.entity.SysUser;
import com.dzics.kanban.model.response.Result;
import com.dzics.kanban.model.response.UserTokenMsg;
import com.dzics.kanban.service.SysUserServiceDao;
import com.dzics.kanban.service.impl.MyUserDetails;
import com.dzics.kanban.service.impl.MyUserDetailsServiceImpl;
import com.dzics.kanban.util.RedisKey;
import com.dzics.kanban.util.RedisUtil;
import com.dzics.kanban.util.privatekey.CreatePrivateKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtAuthService {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    SysUserServiceDao sysUserServiceDao;
    @Autowired
    MyUserDetailsServiceImpl userDetailsService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisUtil redisUtilSetValue;


    /**
     * 登录认证换取JWT令牌
     *
     * @return JWT
     */
    public UserTokenMsg login(String username, String password) throws CustomException {
        try {
            UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(username, password);
            Authentication authentication = authenticationManager.authenticate(upToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (AuthenticationException e) {
            if (e instanceof BadCredentialsException) {
                log.warn("用户登录验证失败：" + e.getMessage());
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR
                        , "账号或密码错误");
            }
            if (e instanceof LockedException) {
                throw new CustomException(CustomExceptionType.AUTHEN_TICATIIN_FAILURE
                        , "用户已禁用");
            }
            if (e instanceof DisabledException) {
                log.warn("用户登录验证失败：" + e.getMessage());
                throw new CustomException(CustomExceptionType.USER_IS_LOCK
                        , CustomExceptionType.USER_IS_LOCK.getTypeDesc());
            }
            log.warn("用户登录验证失败：" + e.getMessage());
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, e.getMessage());
        }
        MyUserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtTokenUtil.generateToken(userDetails);
    }

    public Result refreshToken(String refToken, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        if (!jwtTokenUtil.isTokenExpiredRef(refToken, sub, byUserName)) {
            try {
                UserTokenMsg userTokenMsg = jwtTokenUtil.refreshToken(refToken, sub, byUserName);
                return new Result(CustomExceptionType.OK, userTokenMsg);
            } catch (CustomException e) {
                return new Result(e.getCode(), e.getMessage());
            } catch (Exception e) {
                return new Result(CustomExceptionType.AUTHEN_TOKEN_REF_IS_ERROR.getCode(), CustomExceptionType.AUTHEN_TOKEN_REF_IS_ERROR.getTypeDesc());
            }
        } else {
            return new Result(CustomExceptionType.AUTHEN_TOKEN_REF_IS_ERROR.getCode(), CustomExceptionType.AUTHEN_TOKEN_REF_IS_ERROR.getTypeDesc());
        }
    }

    public Result signout(String sub) {
        String secrity = CreatePrivateKey.privateKey(124);
        String refSecrity = CreatePrivateKey.privateKey(124);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", sub);
        SysUser sysUser = new SysUser();
        sysUser.setSecret(secrity);
        sysUser.setRefSecret(refSecrity);
        sysUserServiceDao.update(sysUser, queryWrapper);
        redisUtilSetValue.del(RedisKey.USER_NAME_AND_USER_TYPE + sub);
        redisUtilSetValue.del(RedisKey.STRING + sub);
        return new Result(CustomExceptionType.OK);
    }

}
