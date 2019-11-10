package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


public interface UserApi {
    @PostMapping("query")
    public User queryUser(@RequestParam("username")String username,@RequestParam("password") String password);





}