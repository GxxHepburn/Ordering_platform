package com.gxx.ordering_platform.service;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.utils.JWTUtils;

@Component
public class MmaService {
	
	@Autowired MmaMapper mmaMapper;
	
	public String login(String username, String password) {
		//根据用户名，密码和数据库比对，如果正确，则进入登陆程序，同时获得该用户手机号(用户名）放入token中
		//如果不正确，则返回登陆失败信息
		JSONObject newJsonObject = new JSONObject();
		if (this.checkingMma(username, password)) {
			//设置一个jwt
			Map<String, Object> claims = new HashMap<String, Object>();
			claims.put("username", username);
			String jwtString = JWTUtils.createJwt(username, 86400000, claims);
			
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 200);
			metaJsonObject.put("msg", "登陆成功");
			
			JSONObject dataJsonObject = new JSONObject();
			dataJsonObject.put("token", jwtString);
			dataJsonObject.put("username", username);
			
			newJsonObject.put("data", dataJsonObject);
			newJsonObject.put("meta", metaJsonObject);
			
		} else {
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 401);
			newJsonObject.put("meta", metaJsonObject);
		}
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public boolean checkingMma(String username, String password) {
		Mmngct mmngct = mmaMapper.getByUsername(username);
		if (mmngct != null) {
			if (password.equals(mmngct.getMMA_Password())) {
				return true;
			}
		}
		return false;
	}
}
