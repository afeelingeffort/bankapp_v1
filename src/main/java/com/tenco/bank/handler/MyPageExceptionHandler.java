package com.tenco.bank.handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.tenco.bank.handler.exception.CustomPageException;

/*
 * View 렌더링을 위해 ModelAndView
 * 객체를 반환하도록 기본 설정되어 있다.
 * 예외 page를 리턴하도록 활용 예정
 * */
@ControllerAdvice
public class MyPageExceptionHandler {

	// 사용자 정의 클래스 활용
	@ExceptionHandler(CustomPageException.class)
	public ModelAndView handleRuntimePageException(CustomPageException e) {
		// ModelAndView 활용 방법
		// suffix
		// preffix
		ModelAndView modelAndView = new ModelAndView("errorPage");
		// modelAndView에 값을 실어 보낼 수 있다. ""<- 안에 쓴 거랑 jsp 파일 ${}랑 연결
		modelAndView.addObject("statusCode", HttpStatus.NOT_FOUND.value());
		modelAndView.addObject("message", e.getMessage());
		
		return modelAndView;
	}

}
