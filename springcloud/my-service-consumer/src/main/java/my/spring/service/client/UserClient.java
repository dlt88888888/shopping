package my.spring.service.client;

import my.spring.service.configuration.FeignLogConfiguration;
import my.spring.service.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value ="service-provider",fallback =UserClientFallback.class,configuration = FeignLogConfiguration.class)
public interface UserClient {
    @GetMapping("user/{id}")
    public User queryById(@PathVariable("id") long id);
}
