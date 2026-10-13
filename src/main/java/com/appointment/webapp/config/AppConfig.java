package com.appointment.webapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map /resources/** to classpath:/static/resources/
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/resources/");

        // Map /css/** to classpath:/static/css/
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");

        // Map /js/** to classpath:/static/js/
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");

        // Map /images/** to classpath:/static/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // Map /adminlte/** to classpath:/static/adminlte/
        registry.addResourceHandler("/adminlte/**")
                .addResourceLocations("classpath:/static/adminlte/");
    }

}
