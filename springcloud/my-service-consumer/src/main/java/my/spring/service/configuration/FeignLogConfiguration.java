package my.spring.service.configuration;

import feign.Logger;
import org.springframework.context.annotation.Configuration;


@Configuration
public class FeignLogConfiguration {
    Logger.Level feignLoggerLevel(){
        return Logger.Level.FULL;
    }

}
