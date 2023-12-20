package com.funnyproject.todolistlistapi.interceptor;

import com.funnyproject.todolistlistapi.AppConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    private final AppConfig appConfig;

    public InterceptorConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Bean
    public TokenInterceptor tokenInterceptor() {
        return new TokenInterceptor(appConfig);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor())
                .addPathPatterns("/lists/**");
    }
}
