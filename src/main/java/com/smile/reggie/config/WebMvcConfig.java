package com.smile.reggie.config;

import com.smile.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/ ");
    }

    @Override
    protected void extendMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        log.info("拓展消息转换器。。。");
        MappingJackson2HttpMessageConverter messageConverter=new MappingJackson2HttpMessageConverter();
       messageConverter.setObjectMapper(new JacksonObjectMapper());
       converters.add(0,messageConverter);

    }
}
