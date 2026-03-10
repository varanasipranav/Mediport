package com.ezc.aragenPR.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@EnableWebMvc 

@SpringBootApplication
@EnableConfigurationProperties  
@EnableScheduling//Kavya
@PropertySource("classpath:application.properties")
public class Application extends SpringBootServletInitializer{
//Author :: Radha & Pratik 
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	   return application.sources(Application.class);
    }

}
