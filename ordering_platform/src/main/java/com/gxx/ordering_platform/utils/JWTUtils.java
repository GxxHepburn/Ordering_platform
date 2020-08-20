package com.gxx.ordering_platform.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.tomcat.util.codec.binary.Base64;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtils {

	public static String createJwt(String subject, long ttlMillis, Map<String, Object> claims) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date nowDate = new Date(nowMillis);
		SecretKey key = generalKey();
		JwtBuilder builder = Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(nowDate)
				.setSubject(subject)
				.signWith(signatureAlgorithm, key);
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date expDate = new Date(expMillis);
			builder.setExpiration(expDate);
		}
		return builder.compact();
	}
	
	public static Claims parseJwt(String jwt) {
		SecretKey key = generalKey();
		Claims claims = Jwts.parser()
				.setSigningKey(key)
				.parseClaimsJws(jwt).getBody();
		return claims;
	}
	
	public static SecretKey generalKey() {
		SecretKey key = new SecretKeySpec(PropertiesUtils.get("jwtSaltValue", "jwtSaltValue").getBytes(), "AES");
		return key;
	}
}
