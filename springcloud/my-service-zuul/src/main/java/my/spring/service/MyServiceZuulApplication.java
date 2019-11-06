package my.spring.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@EnableZuulProxy  //开启网关功能
@EnableDiscoveryClient
public class MyServiceZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyServiceZuulApplication.class, args);
    }

}
