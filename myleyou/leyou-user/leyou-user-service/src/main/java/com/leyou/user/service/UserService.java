package com.leyou.user.service;

import com.leyou.common.utils.CodecUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.awt.font.TextHitInfo;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:code:phone";

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public Boolean checkData(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }

        return this.userMapper.selectCount(record) == 0;
    }

    public Boolean sendVerifyCode(String phone) {

        //生成验证码
        String code = NumberUtils.generateCode(6);

        try {
            HashMap<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            //leyou.sms.exchange   交换机名称   sms.verify.code 是routing key  把消息路由到不同队列
            //发送短信
            //this.amqpTemplate.convertAndSend("leyou.sms.exchange","sms.verify.code",msg);

            this.redisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.HOURS);


            System.out.println("发送成功    phone: " + phone + "code:" + code);
            return true;
        } catch (AmqpException e) {

            logger.error("发送短信失败。phone:{}, code:{}", phone, code, e);
            return false;
        }


    }

    public Boolean register(User user, String code) {
        //校验短信验证码

        String cacheCode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code, cacheCode)) {
            return false;
        }

        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //对密码加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //强制设置不能指定的参数为空
        user.setId(null);

        user.setCreated(new Date());

        //添加到数据库
        Boolean b = this.userMapper.insertSelective(user) == 1;

        if (b) {
            //注册成功
            this.redisTemplate.delete(KEY_PREFIX + user.getPhone());
        }

        return b;

    }

    public Boolean login(User user) {

        User user1 = new User();
        int type = type(user.getUsername());
        Boolean bo = false;
        switch (type) {
            case 0:
                break;
            case 1:
                bo = true;
                user1.setPhone(user.getUsername());
                break;
            default:
                bo = true;
                user1.setUsername(user.getUsername());
                break;
        }
        if(queryUserByAllType(user1,user.getPassword())==null){
            bo=false;
        }


        return bo;
    }

    public int type(String str) {

        String phone = "^[1](([3][0-9])|([4][5,7,9])|([5][0-9])|([6][6])|([7][3,5,6,7,8])|([8][0-9])|([9][8,9]))[0-9]{8}$";// 验证手机号
        Pattern p = Pattern.compile(phone);
        Matcher m = p.matcher(str);
        boolean b = m.matches();
        if (b) {
            return 1; //手机号
        }

        return 2;
    }


    public User queryUserByAllType(User user,String password){
        User u = this.userMapper.selectOne(user);
        if(u==null){
            return null;
        }

        if(!u.getPassword().equals(CodecUtils.md5Hex(password,u.getSalt()))){
            return null;
        }

        return u;

    }

    public User queryUser(String username, String password) {

        int type = type(username);
        User user = new User();
        if(type==1){
            user.setPhone(username);
        }else if(type==2){
            user.setUsername(username);
        }

        User u = this.userMapper.selectOne(user);
        if(!u.getPassword().equals(CodecUtils.md5Hex(password,u.getSalt()))){
            return null;
        }

        return u;


    }

}
