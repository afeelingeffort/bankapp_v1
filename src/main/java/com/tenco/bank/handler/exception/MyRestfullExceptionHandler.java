package com.tenco.bank.handler.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/*
 * 예외 시
 * 데이터를 내려줄 수 있다.
 * */
@RestControllerAdvice // IoC 대상 + AOP기반
public class MyRestfullExceptionHandler {

	@ExceptionHandler(Exception.class)
	public void exception(Exception e) {
		System.out.println(e.getClass().getName());
		System.out.println(e.getMessage());
	}
	
	// 사용자 정의 예외 클래스 활용
	@ExceptionHandler(CustomRestfullException.class)
	public String basicException(CustomRestfullException e) {
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		// 반드시 마지막에 세미콜론을 붙여야 한다.
		sb.append("alert('"+e.getMessage()+"');");
		sb.append("history.back();");
		sb.append("</script>");
		
		return sb.toString();
	}
	
	@ExceptionHandler(UnAuthorizedException.class)
	public String unAuthorizedException(UnAuthorizedException e) {
		StringBuffer sb = new StringBuffer();
		sb.append("<script>");
		// 반드시 마지막에 세미콜론을 붙여야 한다.
		sb.append("alert('"+e.getMessage()+"');");
		sb.append("location.href='/user/sign-in';");
		sb.append("</script>");
		
		return sb.toString();
	}
	
}
