package com.travelshare.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/test",
                        "/home",
                        "/upload/**",
                        "/strategy/list",
                        "/strategy/detail/**",
                        "/strategy/search",
                        "/travel-notice/list",
                        "/travel-notice/detail/**",
                        "/travel-notice/search/**",
                        "/travel-notice/warning/**",
                        "/route/list",
                        "/route/detail/**",
                        "/route/province",
                        "/route/city",
                        "/partner/list",
                        "/partner/detail/**",
                        "/partner/city",
                        "/city/list",
                        "/city/detail/**",
                        "/city/search",
                        "/attraction/list",
                        "/attraction/detail/**",
                        "/attraction/search",
                        "/comment/list",
                        "/uploads/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射：上传的图片可通过 /uploads/** 访问
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
