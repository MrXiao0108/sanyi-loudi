package com.dzics.business.auth;

import com.dzics.business.auth.jwt.JwtAuthenticationTokenFilter;
import com.dzics.business.service.impl.MyUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    MyLogoutSuccessHandler myLogoutSuccessHandler;

    @Resource
    MyUserDetailsServiceImpl myUserDetailsServiceImpl;

    @Resource
    private DataSource datasource;

    @Resource
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Resource
    private MyAccessDeniedHandler accessDeniedHandler;
    @Resource
    private MyAuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    MyBCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers("/auth/authentication")
                .and().cors().configurationSource(corsConfigurationSource()).and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/signout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessHandler(myLogoutSuccessHandler)
                .and().rememberMe()
                .rememberMeParameter("remember-me-token")
                .rememberMeCookieName("remember-me-cookie")
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .tokenRepository(persistentTokenRepository())
                .and()
                .authorizeRequests()
//                无需登陆访问
                .antMatchers(
                        "/**",
                        "/api/agv/dispatch/car/click/log",
                        "/api/general/intelligent/detection/getAllLines",
                        "/api/general/intelligent/detection/getAllProducts",
                        "/api/general/intelligent/detection/list",
                        "/test/**",
                        "/api/mom/order/transPond/force/close",
                        "/api/mom/order/position",
                        "/api/qrcode/dispatch/**",
                        "/api/product/check/addToolConfigure/*",
                        "/api/agv/dispatch/material/frid",
                        "/api/agv/order/check"
                        , "/api/agv/call/material",
                        "/api/agv/dispatch/car/click/signal",
                        "/api/agv/dispatch/car/click/confirm",
                        "/api/agv/dispatch/material/history",
                        "/api/system/run/model/lock/screen/password",
                        "/api/methods",
                        "/upload/product/detection",
                        "/test/send/udp",
                        "/api/methods",
                        "/api/user/auth/login",
                        "/favicon.ico",
                        "/doc.html",
                        "/webjars/**",
                        "/swagger-resources",
                        "/api/production/quantity/**",
                        "/api/kanban/**",
                        "/v2/**").permitAll()
                .antMatchers(
                        "/api/upWork/station",
                        "/api/product/check/trend/getDepartId/*",
                        "/collection/dzDataDevice/getDzEquipment",
                        "/api/location/artifacts/get/**",
                        "/api/location/artifacts/productNo/**",
                        "/api/product/check/getToolConfigureById/**",
                        "/api/commons/**",
                        "/api/product/check/getEquipmentByLine/*",
                        "/api/index/**",
                        "/line/all/line/list",
                        "/api/index/get/socket/address",
                        "/api/user/auth/switch/site",
                        "/api/user/auth/getInfo",
                        "/api/user/auth/getRouters",
                        "/api/user/auth/you/can/switch/site",
                        "/api/user/auth/signout",
                        "/api/user/info",
                        "/api/user/info/*").authenticated()//只有登陆才能访问，不需要鉴权
                .anyRequest().access("@rabcService.hasPermission(request,authentication)")
                .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint);
        http.csrf().disable();
    }


    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    public void configure(WebSecurity web) {
        //将项目中静态资源路径开放出来
        web.ignoring()
                .antMatchers("static/**", "/css/**", "/fonts/**", "/img/**", "/js/**");
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(datasource);
        return tokenRepository;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT"));
        configuration.applyPermitDefaultValues();
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
