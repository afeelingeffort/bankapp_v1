package com.tenco.bank.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class TimestampUtil {

	public static String timestampToString(Timestamp timestamp) {
		
		// 문자열 포맷
		// yyyy-MM-dd HH:mm:ss
		
		// format 안에 timestamp를 넣으면 바뀐다.
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
		
		return sdf;
	}
	
}
