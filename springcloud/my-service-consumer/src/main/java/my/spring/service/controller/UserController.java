package my.spring.service.controller;

import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import my.spring.service.client.UserClient;
import my.spring.service.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
@RequestMapping("consumer/user")
//@DefaultProperties(defaultFallback = "fallBackMethod") //全局访问失败回调
public class UserController {
   /* @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private DiscoveryClient discoveryClient;//eureka客户端，获取服务信息
*/
    @Autowired
    private UserClient userClient;

    @GetMapping
    @ResponseBody
   // @HystrixCommand(fallbackMethod = "queryUserByIdFallBack")
    //@HystrixCommand
    public User queryUserById(@RequestParam("id") long id) {

        /*if(id==5){
            throw new RuntimeException("服务器正忙");
        }*/

        User user = this.userClient.queryById(id);

        //List<ServiceInstance> instances = discoveryClient.getInstances("service-provider");
        //ServiceInstance instance = instances.get(0);
        //String baseUrl="http://"+instance.getHost()+":"+instance.getPort()+"/user/"+id;
       // String baseUrl="http://service-provider/user/"+id;
       // User user = this.restTemplate.getForObject(baseUrl, User.class);
        //User user = this.restTemplate.getForObject("http://localhost:8082/user/" + id, User.class);
        //return user.toString();
        return user;
    }


    /*public  String queryUserByIdFallBack(long id){
        return "请求繁忙，请稍后再试";
    }*/

    /*public String fallBackMethod(){
        return "请求繁忙，请稍后再试啊";
    }*/
}
