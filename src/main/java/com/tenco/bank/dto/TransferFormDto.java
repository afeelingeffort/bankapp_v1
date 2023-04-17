package com.tenco.bank.dto;

import lombok.Data;

@Data
public class TransferFormDto {

	private long amount;
	private String wAccountNumber;
	private String dAccountNumber;
	private String wAccountPassword;
	
}
