package com.leyou.user.controller;

import com.leyou.common.utils.CookieUtils;
import com.leyou.user.pojo.User;
import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 校验数据是否可用
     *
     * @param data
     * @param type
     * @return
     */
    @GetMapping("check/{data}/{type}")
    public ResponseEntity<Boolean> checkUserData(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bo = this.userService.checkData(data, type);
        if (bo == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(bo);
    }


    /**
     *
     * 注册功能
     * @param user
     * @param code
     * @return
     */
    @PostMapping("register")
    public ResponseEntity<Void> register(@Valid User user, @RequestParam("code") String code) {
        Boolean bo = this.userService.register(user, code);
        if(bo==null||!bo){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /**
     * 发送手机验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("code")
    public ResponseEntity<Void> sendVerifyCode(String phone) {
        Boolean bo = this.userService.sendVerifyCode(phone);
        if (bo == null || !bo) {

            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    /*@PostMapping("login")
    public ResponseEntity<Void> login(User user){

       Boolean bo= this.userService.login(user);

       if(bo==null||!bo){
           return  new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
       }

       return new ResponseEntity<>(HttpStatus.OK);

    }*/

    @PostMapping("query")
    public ResponseEntity<User> queryUser(@RequestParam("username")String username,@RequestParam("password") String password){

        User u = this.userService.queryUser(username,password);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(u);

    }

    @GetMapping("logout")
    public ResponseEntity<Void> exit(HttpServletRequest request, HttpServletResponse response) {

        Cookie[] cookies = request.getCookies();

        if(cookies!=null&&cookies.length!=0){

            CookieUtils.setCookie(request,response,cookies[0].getName(),null,0);


        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
