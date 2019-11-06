package my.springcloud.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class MyServiceEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyServiceEurekaApplication.class, args);
    }

}
