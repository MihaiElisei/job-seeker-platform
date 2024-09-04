package com.itschool.job_seeker.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    /**
     * Provides a ModelMapper bean for the Spring application context.
     *
     * This method is annotated with @Bean, which means it will return an object that
     * should be registered as a bean in the Spring application context.
     * ModelMapper is used for object mapping, which is particularly useful for
     * transferring data between different layers in an application, such as converting
     * between entity classes and data transfer objects (DTOs).
     *
     * @return a configured instance of ModelMapper
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper;
    }
}
