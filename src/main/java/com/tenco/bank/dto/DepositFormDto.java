package com.tenco.bank.dto;

import lombok.Data;

@Data
public class DepositFormDto {

	private long amount;
	private String dAccountNumber; // 계좌번호
	
}
