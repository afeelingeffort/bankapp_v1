package com.tenco.bank.controller;

import java.io.File;
import java.util.UUID;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.tenco.bank.dto.SignInFormDto;
import com.tenco.bank.dto.SignUpFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired // DI 처리 -> 생성될 때 같이 주소값 생성하며 올라감.
	private UserService userService;

	// signIn에 사용
	@Autowired // DI 처리
	private HttpSession session;

	// http://localhost:8080/user/sign-up
	@GetMapping("/sign-up")
	public String signUp() {
		// prifix
		// subfix 타서
		return "/user/signUp";
	}

	// MIME TYPE : x-www-form-urlencoded
	// form : Query String 방식으로 들어온다.
	// dto : object mapper 처리
	/*
	 * 회원가입 처리
	 * 
	 * @param signUpFormDto
	 * 
	 * @return 리다이렉트로 로그인 페이지
	 */
	// @RequestParam MultipartFile file
	@PostMapping("/sign-up")
	public String signUpProc(SignUpFormDto signUpFormDto) {

		// image/png , tmp.png / 9156
//		System.out.println(signUpFormDto.getFile().getContentType());
//		System.out.println(signUpFormDto.getFile().getOriginalFilename());
//		System.out.println(signUpFormDto.getFile().getSize());

//		// 1. 유효성 검사
//		if (signUpFormDto.getUsername() == null || signUpFormDto.getUsername().isEmpty()) {
//			throw new CustomRestfullException("username을 입력해주세요.", HttpStatus.BAD_REQUEST);
//		}
//
//		if (signUpFormDto.getPassword() == null || signUpFormDto.getPassword().isEmpty()) {
//			throw new CustomRestfullException("password를 입력해주세요.", HttpStatus.BAD_REQUEST);
//		}
//
//		if (signUpFormDto.getFullname() == null || signUpFormDto.getFullname().isEmpty()) {
//			throw new CustomRestfullException("fullname을 입력해주세요.", HttpStatus.BAD_REQUEST);
//		}

		// 사용자 프로필 이미지는 옵션값으로 설정할 예정
		MultipartFile file = signUpFormDto.getFile();
		if (file.isEmpty() == false) {
			// 사용자가 이미지를 업로드 했다면 기능 구현해야 함.
			// 파일 사이즈 체크: 기본 설정 20MB까지 가능함.
			if (file.getSize() > Define.MAX_FILE_SIZE) {
				throw new CustomRestfullException("파일 크기가 20MB 이상일 수 없습니다.", HttpStatus.BAD_REQUEST);
			}
			// 확장자 검사 가능
			
			try {
				// 파일 저장 기능 구현 - 업로드 파일은 HOST 컴퓨터 다른 폴더로 관리
				String saveDirectory = Define.UPLOAD_DIRECTORY;
				// 폴더가 없다면 오류 발생 (파일 생성시)
				File dir = new File(saveDirectory);
				if (dir.exists() == false) {
					dir.mkdirs(); // 폴더가 없으면 폴더 생성
				}

				UUID uuid = UUID.randomUUID();
				// 밑에 형식으로 폴더에 들어감
				String fileName = uuid + "_" + file.getOriginalFilename();
				// 전체 경로를 지정
				// File.separator = / 같은 거라고 생각하자.
				String uploadPath = Define.UPLOAD_DIRECTORY + File.separator + fileName;
				// 만들어짐
				File destination = new File(uploadPath);
				
				// 업로드한 파일을 로컬 저장소에 저장하는 방법
				file.transferTo(destination);
				
				// 따로 구분
				// 객체 상태 변경(dto) 
				signUpFormDto.setOriginFileName(file.getOriginalFilename());
				signUpFormDto.setUploadFileName(fileName);
				
			} catch (Exception e) {
				System.out.println("File업로드에서 오류났어.");
				e.getStackTrace();
			}

			
		}

		// 서비스 호출
		userService.createUser(signUpFormDto);

		return "redirect:/user/sign-in";
	}

	/*
	 * 로그인 폼
	 * 
	 * @return 로그인 페이지
	 */
	@GetMapping("/sign-in")
	public String signIn() {
		return "/user/signIn";
	}

	/*
	 * 로그인 처리
	 * 
	 * @param signInFormDto
	 * 
	 * @return 메인 페이지 이동 (수정 예정) 생각해보기 GET 방식 처리는 브라우저 히스토리에 남겨지기 때문에 예외적으로 로그인 POST
	 * 방식으로 처리한다.
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInFormDto signInFormDto) {
		// todo 변경 예정

		// 유효성 검사 (인증 검사가 있다면 인증 검사가 더 우선)
		if (signInFormDto.getUsername() == null || signInFormDto.getUsername().isEmpty()) {
			throw new CustomRestfullException("username을 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		if (signInFormDto.getPassword() == null || signInFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("password를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		// 사용자 정보 세션에 저장
		User principal = userService.signIn(signInFormDto);
		principal.setPassword(null); // 비밀번호는 보안상 null 처리
		session.setAttribute(Define.PRINCIPAL, principal);

		return "redirect:/account/list";
	}

	@GetMapping("/logout")
	public String logout() {
		session.invalidate();

		return "redirect:/user/sign-in";
	}
}
