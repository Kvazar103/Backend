package com.example.backend.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        String home = System.getProperty("user.home");

        String path=home+ File.separator+"Desktop"+File.separator+"real_estate_project"+
                File.separator+"Backend"+File.separator+"src"+File.separator+"main"+
                File.separator+"java"+File.separator+"com"+File.separator+"example"+
                File.separator+"backend"+File.separator+"images"+File.separator;/// то шукає файл має за цією директорією
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///"+path);
    }
}
