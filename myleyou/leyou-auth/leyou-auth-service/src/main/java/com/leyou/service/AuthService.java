package com.leyou.service;

import com.leyou.client.UserClient;
import com.leyou.common.pojo.UserInfo;
import com.leyou.common.utils.JwtUtils;
import com.leyou.properties.JwtProperties;
import com.leyou.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserClient userClient;

    @Autowired
    private JwtProperties jwtProperties;

    public String authentication(String username, String password) {

        try {

            User user = this.userClient.queryUser(username,password);

            if(user==null){
                return  null;
            }

            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), jwtProperties.getPrivateKey(), jwtProperties.getExpire());

            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
