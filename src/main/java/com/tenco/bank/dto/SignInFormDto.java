package com.tenco.bank.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;

@Data
public class SignInFormDto {

	private String username;
	private String password;

	// 암호화 로그인 처리
//	public boolean checkPassword(String rawPassword, PasswordEncoder passwordEncoder) {
//		System.out.println("11111111111" + passwordEncoder.matches(rawPassword, this.password));
//		return passwordEncoder.matches(rawPassword, this.password);
//	}
}
