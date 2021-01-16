package com.gxx.ordering_platform;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

public class Test {

	public static void main(String[] args) {

		SimpleDateFormat simpleDateFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
		String P_Time_End = simpleDateFormatParse.format(new Date());
		System.out.println(P_Time_End);
	}
}
