package com.example.userservice.config;

import com.example.userservice.security.JwtTokenInterceptor;
import com.example.userservice.utils.CookieUtils;
import com.example.userservice.utils.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenUtils tokenUtils;
    private final CookieUtils cookieUtils;
    private final RedisTemplate redisTemplate;

    @Value("${spring.url}")
    private static String URI;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor())
                .excludePathPatterns(URI + "/swagger-resources/**", URI + "/swagger-ui/**", URI + "/v3/api-docs", URI + "/api-docs/**")
                .excludePathPatterns("/swagger-resources/**", "/swagger-ui/**", "/v3/api-docs", "/api-docs/**")
                .excludePathPatterns("/signUp", "/signIn", "/error/**", "/reissue")
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowCredentials(true)
                .maxAge(3600)
                .allowedOriginPatterns("http://localhost:3000")
                .allowedMethods("OPTIONS", "GET", "POST", "PUT", "DELETE");
    }


    @Bean
    public JwtTokenInterceptor jwtTokenInterceptor(){
        return new JwtTokenInterceptor(tokenUtils, cookieUtils, redisTemplate);
    }
}
