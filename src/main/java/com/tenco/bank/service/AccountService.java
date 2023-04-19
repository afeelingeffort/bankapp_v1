package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.WithdrawFormDto;
import com.tenco.bank.handler.exception.CustomRestfullException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;

@Service // IoC 대상 + 싱글톤
public class AccountService {

	@Autowired // DI 처리 잊지마
	private AccountRepository accountRepository;

	@Autowired
	private HistoryRepository historyRepository;

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

	// 출금 기능 로직 고민해보기 -> 하나의 작업의 단위니까 @Transactional 걸어주기
	// 1. 계좌 존재 여부 확인 -> select query -> wAccountNumber
	// 2. 본인 계좌 여부 확인
	// 3. 계좌 비번 확인
	// 4. 잔액 여부 확인
	// 5. 출금 처리 -> update query
	// 6. 거래내역 등록 -> insert query
	// 7. 트랜잭션 처리
	@Transactional
	public void updateAccountWithdraw(WithdrawFormDto withdrawFormDto, Integer principalId) {

		// 1. 해당 계좌 존재 여부
		Account accountEntity = accountRepository.findByNumber(withdrawFormDto.getWAccountNumber());
//		System.out.println(accountEntity.toString());
		if (accountEntity == null) {
			throw new CustomRestfullException("계좌가 없습니다.", HttpStatus.BAD_REQUEST);
		}

		// 2. 본인 계좌 여부 확인
		if (accountEntity.getUserId() != principalId) {
			throw new CustomRestfullException("본인 소유 계좌가 아닙니다.", HttpStatus.UNAUTHORIZED);
		}

		// 3. 계좌 비번 확인 
		// ??
		// T == F --> F
		if (accountEntity.getPassword().equals(withdrawFormDto.getWAccountPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED);
		}

		// 4. 잔액 여부 확인
		if (accountEntity.getBalance() < withdrawFormDto.getAmount()) {
			throw new CustomRestfullException("계좌 잔액이 부족합니다.", HttpStatus.BAD_REQUEST);
		}

		// 5. 출금 처리 UPDATE -> 모델 객체 상태값 변경 처리
		// 잔액 - 거래 금액
//		accountEntity.setBalance(accountEntity.getBalance() - withdrawFormDto.getAmount());
		accountEntity.withdraw(withdrawFormDto.getAmount());
		accountRepository.updateById(accountEntity);

		// 6. 거래 내역 등록
		History history = new History();
		/*
		 * insert into history_tb (amount, w_balance, d_balance, w_account_id,
		 * d_account_id, createdAt) values(#{amount}, #{wBalance}, #{dBalance},
		 * #{wAccountId}, #{dAccountId}, now())
		 */
		// 출금이니까 입금은 null 처리?
		history.setAmount(withdrawFormDto.getAmount());
		history.setWBalance(accountEntity.getBalance());
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
		int resultRowCount = historyRepository.insert(history);

		if (resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
