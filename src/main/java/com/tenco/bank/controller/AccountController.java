package com.tenco.bank.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;

@Controller
@RequestMapping("/account")
public class AccountController {

	@Autowired // 의존 주입
	private HttpSession session;

	@Autowired
	private AccountService accountService;

	// todo
	// 계좌 목록 페이지
	// 입금 페이지
	// 출금 페이지
	// 이체 페이지
	// 계좌 상세보기 페이지
	// 계좌 생성 페이지

	// http://localhost:8080/account/list
	// http://localhost:8080/account/
	/*
	 * 계좌 목록 페이지
	 * 
	 * @return 목록 페이지 이동
	 */
	@GetMapping({ "list", "/" })
	public String list(Model model) {

		// todo 예외 테스트 - 주석 처리 예정
//		throw new CustomRestfullException("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
//		throw new CustomPageException("페이지를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

//		// 인증검사 처리
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요.", HttpStatus.UNAUTHORIZED);
		}

		// View 화면으로 데이터를 내려주는 기술
		// Model 과 ModelAndView 활용
		// 1. 계좌 데이터가 있거나 없거나
		List<Account> accountList = accountService.readAccountList(principal.getId());

		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}

		// prefix
		// suffix
		return "/account/list";
	}

	// 출금 페이지
	@GetMapping("/withdraw")
	public String withdraw() {
		return "/account/withdrawForm";
	}

	// 입금 페이지
	@GetMapping("/deposit")
	public String deposit() {
		return "/account/depositForm";
	}

	// 이체 페이지
	@GetMapping("/transfer")
	public String transfer() {
		return "/account/transferForm";
	}

	// 계좌 상세보기 페이지
	@GetMapping("/detail")
	public String detail() {
		return "";
	}

	// 계좌 생성 페이지
	@GetMapping("/save")
	public String save() {
		// 인증 검사 처리
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요.", HttpStatus.UNAUTHORIZED);
		}

		return "/account/saveForm";
	}

	/*
	 * 계좌 생성 인증 검사 유효성 검사 처리 - 0원 입력 가능, 마이너스 입력 불가
	 * 
	 * @param SaveFormDto
	 * 
	 * @return 계좌 목록 페이지
	 */
	@PostMapping("/save-proc")
	public String saveProc(SaveFormDto saveFormDto) {

		// 여기서도 인증 검사
		User user = (User) session.getAttribute("principal");
		if (user == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요.", HttpStatus.UNAUTHORIZED);
		}

		// 유효성 검사하기
		if (saveFormDto.getNumber() == null || saveFormDto.getNumber().isEmpty()) {
			throw new CustomRestfullException("계좌번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		if (saveFormDto.getPassword() == null || saveFormDto.getPassword().isEmpty()) {
			throw new CustomRestfullException("비밀번호를 입력해주세요.", HttpStatus.BAD_REQUEST);
		}

		if (saveFormDto.getBalance() == null || saveFormDto.getBalance() < 0) {
			throw new CustomRestfullException("잘못된 금액입니다.", HttpStatus.BAD_REQUEST);
		}

		// 서비스 호출
		// userId는 세션에서 뽑아낼 수 있다
		accountService.createAccount(saveFormDto, user.getId());

		// redirect !!
		return "redirect:/account/list";
	}

}
