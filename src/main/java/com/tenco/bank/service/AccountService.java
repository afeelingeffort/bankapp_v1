package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.DepositFormDto;
import com.tenco.bank.dto.SaveFormDto;
import com.tenco.bank.dto.TransferFormDto;
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
		// String이라서 equals 씀.
		// T == F --> F
		if (accountEntity.getPassword().equals(withdrawFormDto.getWAccountPassword()) == false) {
			throw new CustomRestfullException("출금 계좌 비밀번호가 틀렸습니다.", HttpStatus.UNAUTHORIZED);
		}

		// 4. 잔액 여부 확인
		// 잔액이 출금 금액보다 작으면 실행
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
		// 출금 처리니까 입금은 null 처리
		// amount는 앞에서(dto) 값을 받아온대
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

	// 입금 처리 기능
	// 트랜잭션 처리
	// 1. 계좌 존재 여부 확인
	// 2. 입금 처리 -> update
	// 3. 거래 내역 등록 처리 -> insert
	@Transactional
	public void updateAccountDeposit(DepositFormDto depositFormDto) {

		Account accountEntity = accountRepository.findByNumber(depositFormDto.getDAccountNumber());
		if (accountEntity == null) {
			throw new CustomRestfullException("해당 계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 객체 상태값 변경
		accountEntity.deposit(depositFormDto.getAmount());
		accountRepository.updateById(accountEntity);

		// ?? 헷갈
		History history = new History();
		history.setAmount(depositFormDto.getAmount());
		history.setWBalance(null);
		history.setDBalance(accountEntity.getBalance());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());

		int resultRowCount = historyRepository.insert(history);
		if (resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리가 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// 이체 기능 만들기 -> update
	// 1. 출금 계좌 존재 여부 확인 -> select
	// 2. 입금 계좌 존재 여부 확인 -> select
	// 3. 출금 계좌 본인 소유 확인 -> 1번(Entity) == (principal) 모르겠는데
	// 4. 출금 계좌 비번 확인 -> 1번(Entity) == (Dto)
	// 5. 출금 계좌 잔액 여부 확인 -> 1번(Entity) == (Dto)
	// 6. 출금 계좌 잔액 변경 (이체 기능) -> update
	// 7. 입금 계좌 잔액 변경 (이체 기능) -> update
	// 8. 거래내역 저장 -> insert
	// 트랜잭션 처리
	@Transactional
	public void updateAccountTransfer(TransferFormDto transferFormDto, Integer principalId) {
		Account withdrawAccountEntity = accountRepository.findByNumber(transferFormDto.getWAccountNumber());

		// 1. 출금 계좌 존재 여부
		if (withdrawAccountEntity == null) {
			throw new CustomRestfullException("출금 계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 2. 입금 계좌 존재 여부 확인
		Account depositAccountEntity = accountRepository.findByNumber(transferFormDto.getDAccountNumber());

		if (depositAccountEntity == null) {
			throw new CustomRestfullException("입금 계좌가 존재하지 않습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 3. 출금 계좌 본인 소유 확인 Account.java
		withdrawAccountEntity.checkOwner(principalId);

		// 4.
		withdrawAccountEntity.checkPassword(transferFormDto.getWAccountPassword());

		// 5.
		withdrawAccountEntity.checkBalance(transferFormDto.getAmount());

		// 6. (출금 객체 상태값 변경 - 계좌 잔액 수정 처리)
		withdrawAccountEntity.withdraw(transferFormDto.getAmount());
		// 변경된 객체 상태값으로 DB UPDATE 처리
		accountRepository.updateById(withdrawAccountEntity);

		// 7. (입금 객체 상태값 변경 - 계좌 잔액 수정 처리)
		depositAccountEntity.deposit(transferFormDto.getAmount());

		// 변경된 입금 객체 상태 값으로 DB UPDATE 처리
		accountRepository.updateById(depositAccountEntity);

		// 8. 거래 내역 저장
		History history = new History();
		history.setAmount(transferFormDto.getAmount());
		history.setWAccountId(withdrawAccountEntity.getId());
		history.setDAccountId(depositAccountEntity.getId());
		history.setWBalance(withdrawAccountEntity.getBalance());
		history.setDBalance(depositAccountEntity.getBalance());

		int resultRowCount = historyRepository.insert(history);

		if (resultRowCount != 1) {
			throw new CustomRestfullException("정상 처리 되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
