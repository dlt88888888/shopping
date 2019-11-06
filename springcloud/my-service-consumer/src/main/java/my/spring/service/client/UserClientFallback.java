package my.spring.service.client;


import my.spring.service.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    @Override
    public User queryById(long id) {

        User user=new User();

        user.setName("服务器正忙，请稍后！");

        return user;
    }
}
