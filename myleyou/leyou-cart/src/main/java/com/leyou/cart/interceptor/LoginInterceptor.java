package com.leyou.cart.interceptor;

import com.leyou.cart.config.JwtProperties;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor extends HandlerInterceptorAdapter {


    private JwtProperties jwtProperties;

    //定义一个线程域，存放登录用户

    private static final ThreadLocal<UserInfo> t1=new ThreadLocal<>();


    public LoginInterceptor(JwtProperties jwtProperties){
        this.jwtProperties=jwtProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");

        if(StringUtils.isBlank(token)){
            //未登录，返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        //有token


        try {
             UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
             t1.set(userInfo);
             return true;
        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        t1.remove();
    }

    public static UserInfo getLoginUser(){
        return t1.get();
    }
}
