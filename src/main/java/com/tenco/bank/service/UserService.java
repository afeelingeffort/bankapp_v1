package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.interfaces.UserRepository;

// 서비스는 서비스 ~ 
@Service // IoC 대상
public class UserService {

	@Autowired // DI 처리 (객체 생성시 의존 주입 처리)
	private UserRepository userRepository; // 인터페이스

	// 메서드 호출이 시작될 때 트랜잭션에 시작
	// 메서드 종료시 트랜잭션 종료되면 '커밋'한다.
	// (커밋 : 저장 장치에 실제로 저장한다.)
	@Transactional
	public void signUp(SignUpFormDto signUpFormDto) {
		// 매개변수로 dto 넣음.
		// signUpFormDto, User
		int result = userRepository.insert(signUpFormDto);
		if (result != 1) {
			throw new CustomRestfullException("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

//	@Transactional
//	public void signIn(SignInFormDto signInFormDto) {
//		int result = userRepository.insert(signInFormDto);
//
//		if (result != 1) {
//			throw new CustomRestfullException("로그인 실패", HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//	}
}
