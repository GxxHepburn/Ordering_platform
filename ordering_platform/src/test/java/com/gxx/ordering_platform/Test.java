package com.gxx.ordering_platform;

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;
import com.gxx.ordering_platform.utils.RedisUtil;

public class Test {

	
	public static void main(String[] args) {
		SecureRandom sr = new SecureRandom();
		int checkNum = 100000 + sr.nextInt(900000);
		System.out.println(checkNum);
	}
}
