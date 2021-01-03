package com.gxx.ordering_platform;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

public class Test {

	public static void main(String[] args) {

		String jiami = "";
		try {
			jiami = EncryptionAndDeciphering.encryption("o5C-Y5KCm_mMGH2nyb8IVkxUAs50");
			System.out.println(jiami);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			System.err.println(EncryptionAndDeciphering.deciphering(jiami));
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
