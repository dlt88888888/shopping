package myspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
/*import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;*/

/*@EnableAutoConfiguration
@ComponentScan*/
@SpringBootApplication
public class testApplication {

    public static void main(String[] args) {
        SpringApplication.run(testApplication.class,args);
    }
}
