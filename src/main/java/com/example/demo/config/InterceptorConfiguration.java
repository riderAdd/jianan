package com.example.demo.config;

import com.example.demo.web.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 拦截器配置文件
 *
 * @author : ssk
 * @date : 2020/3/4
 */
@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

	@Bean
	public LoginInterceptor loginInterceptor() {
		return new LoginInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 注册拦截器
		registry.addInterceptor(loginInterceptor()).addPathPatterns("/**");
		super.addInterceptors(registry);
	}

}
