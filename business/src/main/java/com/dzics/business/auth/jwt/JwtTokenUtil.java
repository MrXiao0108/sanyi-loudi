package com.dzics.business.auth.jwt;

import com.dzics.business.model.response.RefTokenMsg;
import com.dzics.business.model.response.UserTokenMsg;
import com.dzics.business.service.impl.MyUserDetailsImpl;
import com.dzics.business.service.impl.MyUserDetailsServiceImpl;
import com.dzics.business.util.RedisUtil;
import com.dzics.common.exception.CustomException;
import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.model.entity.SysUser;
import com.dzics.common.service.SysUserServiceDao;
import com.dzics.common.util.RedisKey;
import com.dzics.common.util.privatekey.CreatePrivateKey;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("ALL")
@Data
@Component
public class JwtTokenUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenUtil.class);
    /**
     * token 有效时间 毫秒
     */
    @Value("${jwt.expiration}")
    private long expiration;
    @Value("${jwt.expirationProxyLower}")
    private long expirationProxyLower;
    /**
     * refToken时长 重新登录时长毫秒
     */
    @Value("${jwt.reamber.me}")
    private long refToken;
    @Value("${jwt.reamber.meProxyLower}")
    private long refTokenProxyLower;
    /**
     * 请求头部key
     */
    @Value("${jwt.header}")
    private String header;
    /**
     * ref Token Header 值
     */
    @Value("${jwt.ref.token_header}")
    private String refTokenHeader;

    @Resource
    private MyUserDetailsServiceImpl userDetailsService;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private SysUserServiceDao sysUserServiceDao;

    /**
     * 生成token令牌
     *
     * @param userDetails
     * @return
     */
    public synchronized UserTokenMsg generateToken(MyUserDetailsImpl userDetails) {
//       请求token
        Map<String, Object> claims = new HashMap<>(3);
        long creatTime = System.currentTimeMillis();
        Date expirationDate = new Date((creatTime + expiration));
        claims.put("sub", userDetails.getUsername());
        claims.put("creatTime", creatTime / 1000);
//        刷新token
        Map<String, Object> refclaims = new HashMap<>(3);
        Date refTokenDate = new Date((creatTime + refToken));
        refclaims.put("sub", userDetails.getUsername());
        refclaims.put("creatTime", creatTime / 1000);

        RefTokenMsg refTokenMsg = new RefTokenMsg();
        refTokenMsg.setCreatTime(creatTime / 1000);
        refTokenMsg.setExpires_in(refTokenDate.getTime() / 1000);
        String secret = CreatePrivateKey.privateKey(124);
        String refSecret = CreatePrivateKey.privateKey(124);
        boolean b = userDetailsService.updateSercityRef(userDetails.getUsername(), refSecret, secret, null);
        String refToken = "";
        String generteToken = "";
        if (b) {
            generteToken = generteToken(claims, expirationDate, secret);
            refToken = generteToken(refclaims, refTokenDate, refSecret);
        }
        UserTokenMsg tokenMsg = new UserTokenMsg();
        log.debug("用户：{} ,创建token->{}" , userDetails.getUsername(),generteToken);
        tokenMsg.setAccess_token(generteToken);
        tokenMsg.setRefresh_token(refToken);
        tokenMsg.setExpires_in(expirationDate.getTime() / 1000);
        tokenMsg.setSub(userDetails.getUsername());
        tokenMsg.setCode(userDetails.getCode());
        tokenMsg.setCreatTime(creatTime / 1000);
        tokenMsg.setRefTokenMsg(refTokenMsg);
        redisUtil.lSet(RedisKey.LEASE_CAR_TOKEN_HISTORY + userDetails.getUsername(), tokenMsg.getAccess_token(), 10);
        return tokenMsg;
    }

    /**
     * 加密生成token
     *
     * @param claims
     * @return
     */
    private String generteToken(Map<String, Object> claims, Date expirationDate, String secret) {
        return Jwts.builder().setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 根据token获取用户名
     *
     * @param token
     * @return
     */
    public String getUserNameFromToken(String token, String sub) {
        SysUser byUserName = sysUserServiceDao.getByUserName(sub);
        Claims claims = getClaimsFromToken(token, byUserName.getSecret());
        return claims.getSubject();
    }

    /**
     * 从令牌数获取数据声明
     *
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token, String secret) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims;
    }

    /**
     * 判断令牌是否过期
     *
     * @param tokenx
     * @param sub
     * @return
     */
    public Boolean isTokenExpired(String tokenx, String sub) {
        try {
            String type = sub.substring(sub.length() - 1);
            String userNum = sub.substring(0, sub.length() - 1);
            SysUser byUserName = sysUserServiceDao.getByUserName(sub);
            Claims claims = getClaimsFromToken(tokenx, byUserName.getSecret());
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            log.warn("验证token是否过期异常：", e);
            if (e instanceof SignatureException) {
                List<String> list = redisUtil.lGet(RedisKey.LEASE_CAR_TOKEN_HISTORY + sub, 0, -1);
                if (!list.isEmpty()) {
                    boolean b = list.stream().anyMatch(token -> token.equals(tokenx));
                    if (b) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 跟新获取时间，刷新token
     *
     * @param refToken
     * @param sub
     * @param byUserName
     * @return
     */
    @Transactional
    public synchronized UserTokenMsg refreshToken(String refToken, String sub, SysUser byUserName) {
        try {
            Object refTime = redisUtil.get(RedisKey.REF_TOEKN_TIME + sub);
            if (refTime != null) {
                long ref = (Long) refTime;
                long currentTimeMillis = System.currentTimeMillis() - 6000;
                if (currentTimeMillis <= ref) {
                    Object tok = redisUtil.get(RedisKey.REF_TOEKN_TIME_TOKEN + sub);
                    if (tok != null) {
                        return (UserTokenMsg) tok;
                    }
                }

            }

            Claims claimOd = getClaimsFromToken(refToken, byUserName.getRefSecret());
            String subject = claimOd.getSubject();
            if (!subject.equals(sub)) {
                throw new CustomException(CustomExceptionType.TOKEN_PERRMITRE_ERROR);
            }
//         生成新的clam
            Map<String, Object> claimNew = new HashMap<>(3);
            String subCla = (String) claimOd.get("sub");
            Integer type = (Integer) claimOd.get("type");
            long currentTimeMillis = System.currentTimeMillis();
            Date expirationDate = new Date(currentTimeMillis + expiration);
            claimNew.put("creatTime", currentTimeMillis / 1000);
            claimNew.put("sub", subCla);
            claimNew.put("type", type);
            String newkey = CreatePrivateKey.privateKey(124);
            boolean upodate = userDetailsService.updateSercity(sub, newkey, null);
            if (upodate) {
                UserTokenMsg tokenMsg = new UserTokenMsg();
                RefTokenMsg refTokenMsg = new RefTokenMsg();
                refTokenMsg.setExpires_in(claimOd.getExpiration().getTime());
                refTokenMsg.setCreatTime((Integer) claimOd.get("creatTime"));
                String token = generteToken(claimNew, expirationDate, newkey);
                tokenMsg.setAccess_token(URLEncoder.encode(token, "UTF-8"));
                tokenMsg.setRefresh_token(URLEncoder.encode(refToken, "UTF-8"));
                tokenMsg.setExpires_in(expirationDate.getTime() / 1000);
                tokenMsg.setSub(byUserName.getUsername());
                tokenMsg.setCreatTime(currentTimeMillis / 1000);
                tokenMsg.setRefTokenMsg(refTokenMsg);
                redisUtil.del(RedisKey.REF_TOEKN_TIME + sub, RedisKey.REF_TOEKN_TIME_TOKEN + sub);
                redisUtil.set(RedisKey.REF_TOEKN_TIME + sub, System.currentTimeMillis());
                redisUtil.set(RedisKey.REF_TOEKN_TIME_TOKEN + sub, tokenMsg);
                redisUtil.lSet(RedisKey.LEASE_CAR_TOKEN_HISTORY + sub, tokenMsg.getAccess_token(), 10);
                return tokenMsg;
            }
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        } catch (Exception e) {
            log.error("刷新token错误", e);
            throw new CustomException(CustomExceptionType.SYSTEM_ERROR);
        }
    }

    /**
     * 验证令牌
     *
     * @param token
     * @param userDetails
     * @param sub
     * @return
     */
    public Boolean validateToken(String token, UserDetails userDetails, String sub, String userName) {
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token, sub));
    }


    public boolean isTokenExpiredRef(String refToken, String sub, SysUser byUserName) {
        try {
            Claims claims = getClaimsFromToken(refToken, byUserName.getRefSecret());
            Date expiration = claims.getExpiration();
            if (!claims.getSubject().equals(sub)) {
                return false;
            }
            return expiration.before(new Date());
        } catch (SignatureException c) {
            throw new CustomException(CustomExceptionType.TOKEN_AUTH_ERROR);
        } catch (Exception e) {
            log.warn("验证token是否过期异常：", e);
            throw new CustomException(CustomExceptionType.AUTHEN_TOKEN_REF_IS_ERROR);
        }
    }
}
