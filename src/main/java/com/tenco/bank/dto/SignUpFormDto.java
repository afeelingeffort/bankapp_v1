package com.tenco.bank.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class SignUpFormDto {

	private String username;
	private String password;
	private String fullname;
	
	// 다중 처리는 배열[]로 사용하자.
	private MultipartFile file; // file은 name file 속성과 일치시켜야 함.
	// 원래 이미지 명, 실제 업로드 된 이미지 명
	private String originFileName;
	private String uploadFileName;
	
}
