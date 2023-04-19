package com.tenco.bank.dto;

import lombok.Data;

@Data
// form name과 이름이 같아야 한다.
public class SaveFormDto {

	private String number;
	private String password;
	private Long balance;
	
}
