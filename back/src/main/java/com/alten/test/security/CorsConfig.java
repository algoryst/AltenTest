package com.alten.test.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Specify the path pattern you want to apply CORS to
                .allowedOrigins("http://localhost:4200") // Allow requests from your Angular app
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH") // Allowed HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true); // If you need to send cookies or auth headers
    }
}