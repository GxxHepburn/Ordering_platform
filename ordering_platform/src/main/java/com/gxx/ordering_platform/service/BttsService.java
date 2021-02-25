package com.gxx.ordering_platform.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.utils.HttpRequestUtils;
import com.gxx.ordering_platform.utils.JWTUtils;

import io.jsonwebtoken.Claims;

@Component
public class BttsService {
	
	@Value("${btts.APIKey}")
	private String APIKey;
	
	@Value("${btts.SecretKey}")
	private String SecretKey;
	
	@Value("${btts.AppID}")
	private String AppID;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public String bttsToken(Map<String, Object> map) {
		String jwtString = map.get("tokenMy").toString();
		
		JSONObject returnJsonObject = new JSONObject();
		Claims claims = null;
		if (jwtString == null) {
			return returnJsonObject.toString();
		}
		try {
			claims = JWTUtils.parseJwt(jwtString);
		} catch (Exception e) {
			// TODO: handle exception
			logger.info("Btts-jwt拦截器，拦截claims: " + claims);
			return returnJsonObject.toString();
		}
		
		String getTokenUrl = "https://aip.baidubce.com/oauth/2.0/token";
		
		String result = HttpRequestUtils.sendGet(getTokenUrl, "grant_type=client_credentials&client_id=" + APIKey + "&client_secret=" + SecretKey);
		return result;
	}
}
