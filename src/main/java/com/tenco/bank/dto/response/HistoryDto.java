package com.tenco.bank.dto.response;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import com.tenco.bank.utils.MoneyFormatUtil;
import com.tenco.bank.utils.TimestampUtil;

import lombok.Data;

// 새로운 쿼리를 위한 Dto 생성 
@Data
public class HistoryDto {
	private Integer id;
	private Long amount;
	private Long balance;
	private String sender;
	private String receiver;
	private Timestamp createdAt;

	public String formatCreatedAt() {
		return TimestampUtil.timestampToString(createdAt);
	}

	public String formatBalance() {
		return MoneyFormatUtil.moneyFormat(balance);
	}

	public String formatAmount() {
		return MoneyFormatUtil.moneyFormat(amount);
	}
}
