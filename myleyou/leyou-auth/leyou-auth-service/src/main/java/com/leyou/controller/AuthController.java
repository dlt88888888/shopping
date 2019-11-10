package com.leyou.controller;

import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JwtUtils;
import com.leyou.properties.JwtProperties;
import com.leyou.service.AuthService;
import com.leyou.user.pojo.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProperties jwtProperties;


    @PostMapping("accredit")
    public ResponseEntity<Void> authentication(
           // @RequestParam("username") String username,
            //@RequestParam("password") String password,
           @Valid User user,
           HttpServletRequest request,
           HttpServletResponse response
    ){

        //登录校验
        String token = this.authService.authentication(user.getUsername(),user.getPassword());

        if(StringUtils.isBlank(token)){
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //将tokenn写入cookie ，并指定httpOnly和true ，防止通过js获取和修改

        CookieUtils.setCookie(request,response,jwtProperties.getCookieName(),token,jwtProperties.getCookieMaxAge(),null,true);

        return ResponseEntity.ok().build();

    }


    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token ,HttpServletRequest request,HttpServletResponse response){
        try {
            //从token中解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());



            //解析成功重要token信息
            token= JwtUtils.generateToken(userInfo,this.jwtProperties.getPrivateKey(),this.jwtProperties.getExpire());

            //更新cookie中的token
            CookieUtils.setCookie(request, response,this.jwtProperties.getCookieName(),token,this.jwtProperties.getCookieMaxAge());

            //解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }


}
