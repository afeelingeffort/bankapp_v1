package com.tenco.bank.utils;

import java.text.DecimalFormat;

public class MoneyFormatUtil {

	public static String moneyFormat(Long money) {
		DecimalFormat df = new DecimalFormat("#,###");
		String formatNumber = df.format(money);
		return formatNumber + "원";
	}
	
}
