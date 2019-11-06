package my.springcloud.service.mapper;

import my.springcloud.service.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper  extends tk.mybatis.mapper.common.Mapper<User> {
}
