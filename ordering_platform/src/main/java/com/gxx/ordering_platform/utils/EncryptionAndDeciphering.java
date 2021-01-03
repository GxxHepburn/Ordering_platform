package com.gxx.ordering_platform.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionAndDeciphering {

	static byte[] key = PropertiesUtils.get("openidCrypto", "key").getBytes(StandardCharsets.UTF_8);
	public static String encryption(String oldStr) throws GeneralSecurityException{
		
		byte[] oldBytes = oldStr.getBytes(StandardCharsets.UTF_8);
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKey keySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec);
		
		return Base64.getEncoder().encodeToString(cipher.doFinal(oldBytes));
	}
	
	public static String deciphering(String oldStr) throws GeneralSecurityException {
		
		
		byte[] oldBytes = Base64.getDecoder().decode(oldStr);
		
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKey keySpec = new SecretKeySpec(key, "AES");
		cipher.init(Cipher.DECRYPT_MODE, keySpec);
		return new String(cipher.doFinal(oldBytes));
	}
}
