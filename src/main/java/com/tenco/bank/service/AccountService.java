package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.model.Account;

@Service // IoC 대상 + 싱글톤
public class AccountService {

	@Autowired // DI 처리 잊지마
	private AccountRepository accountRepository;

	/*
	 * 계좌 생성 기능
	 * 
	 * @param saveFormDto
	 * 
	 * @param principalId
	 */
	// 서비스 로직 만들기
	@Transactional
	public void createAccount(SaveFormDto saveFormDto, int principalId) {
		// saveFormDto --> 변경, 신규 생성
		// 1단계 레벨
		Account account = new Account();
		account.setNumber(saveFormDto.getNumber());
		account.setPassword(saveFormDto.getPassword());
		account.setBalance(saveFormDto.getBalance());
		account.setUserId(principalId);

		int resultRowCount = accountRepository.insert(account);
		if (resultRowCount != 1) {
			throw new CustomRestfullException("계좌 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	// repository 인터페이스 생성 -> service
	// 계좌 목록 보기 기능
	@Transactional
	public List<Account> readAccountList(Integer userId) {

		List<Account> list = accountRepository.findByUserId(userId);
		return list;
	}

}
