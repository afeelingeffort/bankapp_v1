package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

@Configuration // IoC 등록 2개 이상의 Bean
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired // DI
	private AuthInterceptor authInterceptor;

	// path 추가 이해 못한 듯
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
				.addPathPatterns("/account/**");
//				.addPathPatterns("/auth/**"); // 1. path 더 추가하는 방법
		
		// 인터셉터 등록
//		registry.addInterceptor(new AdminInterceptor());
	}
	
	// 리소스 등록 처리 
	// 서버 컴퓨터에 위치한 Resource를 활용하는 방법 (프로젝트 외부 폴더 접근 방법)
	// c드라이버에 있는 spring_upload 폴더에 접근해야 한다.
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		
		// 서버에서 쓸 패턴 지정 
		// 코드 상에서 /images/uploads/** 쓰면 
		// file://C:\\spring_upload\\bank\\upload/ 찾아감 
		registry.addResourceHandler("/images/uploads/**")
				.addResourceLocations("file:///C:\\spring_upload\\bank\\upload/");
	}
	

	@Bean // IoC 관리 대상으로 만들기
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(); 
	}
	
}
