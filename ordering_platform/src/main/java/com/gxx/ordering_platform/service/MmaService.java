package com.gxx.ordering_platform.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.utils.JWTUtils;
import com.gxx.ordering_platform.utils.RedisUtil;

@Component
public class MmaService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired RedisUtil redisUtil;
	
	@Value("${aliyunMsg.accessKeyId}") String accessKeyId;
	
	@Value("${aliyunMsg.accessKeySecret}") String accessKeySecret;
	
	public String login(Map<String, Object> map) {
		//获取请求参数-用户名，密码
		String username = map.get("username").toString();
		String password = map.get("password").toString();
		//根据用户名，密码和数据库比对，如果正确，则进入登陆程序，同时获得该用户手机号(用户名）放入token中
		//如果不正确，则返回登陆失败信息
		JSONObject newJsonObject = new JSONObject();
		if (this.checkingMma(username, password)) {
			// 判断是不是要验证码
			String userful = redisUtil.get(username + "Useful");
			if (userful == null) {
				//设置一个jwt
				Map<String, Object> claims = new HashMap<String, Object>();
				claims.put("username", username);
				String jwtString = JWTUtils.createJwt(username, 180000, claims);
				
				Mmngct mmngct = mmaMapper.getByUsername(username);
				JSONObject dataJsonObject = new JSONObject();
				dataJsonObject.put("mmngct", new JSONObject(mmngct));
				dataJsonObject.put("token", jwtString);
				
				JSONObject metaJsonObject = new JSONObject();
				metaJsonObject.put("status", 201);
				metaJsonObject.put("msg", "需要验证手机号");
				
				newJsonObject.put("meta", metaJsonObject);
				newJsonObject.put("data", dataJsonObject);
				return newJsonObject.toString();
			}
			// 更新登陆时间
			mmaMapper.updateLastLoginTime(username, new Date());
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
	
	public String sendCheck(Map<String, Object> map) {
		
		String phoneNumbers = map.get("MMA_Phone").toString();
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		IAcsClient client = new DefaultAcsClient(profile);
		
		CommonRequest request = new CommonRequest();
		request.setSysMethod(MethodType.POST);
		request.setSysDomain("dysmsapi.aliyuncs.com");
		request.setSysVersion("2017-05-25");
		request.setSysAction("SendSms");
		request.putQueryParameter("RegionId", "cn-hangzhou");
		
		request.putQueryParameter("PhoneNumbers", phoneNumbers);
		
		try {
			CommonResponse response = client.getCommonResponse(request);
			String getSendStr = response.getData();
			System.out.println(getSendStr);
			System.out.println(response.getHttpStatus());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "";
	}
}
