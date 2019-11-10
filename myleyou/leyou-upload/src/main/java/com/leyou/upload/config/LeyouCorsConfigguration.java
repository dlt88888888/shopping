package com.leyou.upload.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
@Configuration
public class LeyouCorsConfigguration {
    @Bean
    public CorsFilter corsFilter(){
        CorsConfiguration configuration = new CorsConfiguration();
        //允许
        configuration.addAllowedOrigin("http://manage.leyou.com");

        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedMethod("POST");

        configuration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        configurationSource.registerCorsConfiguration("/**",configuration);

        return new CorsFilter(configurationSource);
    }
}
