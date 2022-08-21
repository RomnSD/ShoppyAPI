package com.shoppy.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.shoppy.controller.mapper.EntityMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebMvc
public class WebConfiguration extends WebMvcConfigurationSupport {

    @Bean
    public EntityMapper entityMapper() {
        return Mappers.getMapper(EntityMapper.class);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Optional<HttpMessageConverter<?>> converter = converters
                .stream()
                .filter(AbstractJackson2HttpMessageConverter.class::isInstance)
                .findFirst();

        if (converter.isPresent()) {
            AbstractJackson2HttpMessageConverter jsonConverter = (AbstractJackson2HttpMessageConverter) converter.get();
            jsonConverter.getObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            jsonConverter.getObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        }
    }

}
