package my.springcloud.service.service;

import my.springcloud.service.mapper.UserMapper;
import my.springcloud.service.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User queryById(long id) {
        return  this.userMapper.selectByPrimaryKey(id);
    }
}
