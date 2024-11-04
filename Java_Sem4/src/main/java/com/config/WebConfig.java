package com.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.interceptor.CartInterceptor;
import com.interceptor.HeaderInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private HeaderInterceptor headerInterceptor;
    @Autowired
    private CartInterceptor cartInterceptor;
    
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        
        registry.addInterceptor(headerInterceptor)
                .addPathPatterns("/", 
                                 "/shoppingpage/**", 
                                 "/detailproduct/**", 
                                 "/account/**", 
                                 "/cart/**"); 
        registry.addInterceptor(cartInterceptor)
        .addPathPatterns("/cart/**"); 
       
    }
}
