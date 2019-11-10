package com.leyou.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfiguration {

    @Bean
    public CorsFilter corsFilter(){

        //初始化cors配置对象
        CorsConfiguration config = new CorsConfiguration();
        //允许跨域的域名，若果要携带cookie  不能写*  代表所有域名都可以跨域访问
        config.addAllowedOrigin("http://manage.leyou.com");
        config.addAllowedOrigin("http://www.leyou.com");
        config.setAllowCredentials(true);//允许携带cookie
        config.addAllowedMethod("*");//代表所有的请求方法
        config.addAllowedHeader("*");//允许携带任何头信息

        //初始化cors配置源对象
        UrlBasedCorsConfigurationSource configSource=new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**",config);

        //返回 corsFilter 实例  参数：cors配置对象
        return new CorsFilter(configSource);

    }
}

