package my.spring.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/*@SpringBootApplication
@EnableDiscoveryClient //开启Eureka客户端
@EnableCircuitBreaker //开启熔断*/
@SpringCloudApplication  //包含 @SpringBootApplication @EnableDiscoveryClient @EnableCircuitBreaker
@EnableFeignClients //开启feign客户端
public class MyServiceConsumerApplication {

    /*@Bean
    @LoadBalanced
    public RestTemplate restTemplate() {

        return new RestTemplate();
    }
    //导入feign后自动集成负载均衡
    */

    public static void main(String[] args) {
        SpringApplication.run(MyServiceConsumerApplication.class, args);
    }

}
