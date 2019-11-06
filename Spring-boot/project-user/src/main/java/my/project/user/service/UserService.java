package my.project.user.service;

import my.project.user.mapper.UserMapper;
import my.project.user.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public User queryById(Long id) {
        return this.userMapper.selectByPrimaryKey(id);
    }

    @Transactional
    public void deleteById(Long id) {
        this.userMapper.deleteByPrimaryKey(id);

    }


    public List<User> queryAllUser() {
        List<User> users = this.userMapper.selectAll();
        return users;
    }


}
