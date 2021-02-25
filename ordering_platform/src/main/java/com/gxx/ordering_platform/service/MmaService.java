package com.gxx.ordering_platform.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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
import com.gxx.ordering_platform.entity.Mer;
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
	
	@Value("${aliyunMsg.templateCode}") String templateCode;
	
	@Value("${aliyunMsg.signName}") String signName;
	
	@Value("${aliyunMsg.regionId}") String regionId;
	
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
				// 设置3分钟有效期token
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
			if (username.equals(mmngct.getMMA_UserName()) && password.equals(mmngct.getMMA_Password())) {
				return true;
			}
		}
		return false;
	}
	
	public String sendCheck(Map<String, Object> map) {
		
		JSONObject newJsonObject = new JSONObject();
		
		String phoneNumbers = map.get("MMA_Phone").toString();
		String username = map.get("MMA_UserName").toString();
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		IAcsClient client = new DefaultAcsClient(profile);
		
		CommonRequest request = new CommonRequest();
		request.setSysMethod(MethodType.POST);
		request.setSysDomain("dysmsapi.aliyuncs.com");
		request.setSysVersion("2017-05-25");
		request.setSysAction("SendSms");
		request.putQueryParameter("RegionId", regionId);
		

		// 生成6位验证码
		SecureRandom sr = new SecureRandom();
		int checkNum = 100000 + sr.nextInt(900000);
		String checkNumJson = "{ 'code': '" + checkNum + "' }";
		
		request.putQueryParameter("PhoneNumbers", phoneNumbers);
		request.putQueryParameter("TemplateCode", templateCode);
		request.putQueryParameter("SignName", signName);
		// 放入验证码
		request.putQueryParameter("TemplateParam", checkNumJson);
		CommonResponse response = null;
		try {
			response = client.getCommonResponse(request);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 505);
			metaJsonObject.put("msg", "短信服务商出错了，请联系管理员！");
			
			newJsonObject.put("meta", metaJsonObject);
			return newJsonObject.toString();
		}
		String getSendStr = response.getData();
		JSONObject getSendStrJsonObject = new JSONObject(getSendStr);
		if (!"OK".equals(getSendStrJsonObject.getString("Code"))) {
			if ("isv.BUSINESS_LIMIT_CONTROL".equals(getSendStrJsonObject.getString("Code"))) {
				JSONObject metaJsonObject = new JSONObject();
				metaJsonObject.put("status", 201);
				metaJsonObject.put("msg", "短信发送频率超限，如有需要，请联系管理员!");
				newJsonObject.put("meta", metaJsonObject);
				return newJsonObject.toString();
			}
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 510);
			metaJsonObject.put("msg", "系统短信超限或参数错误，请联系管理员!");
			newJsonObject.put("meta", metaJsonObject);
			return newJsonObject.toString();
		}
		// 根据信息返回给前台，着重注意提示限制短信发送频率
		// 设置有效时间，存入redis:有效期，3分钟，用户名，验证码
		redisUtil.setEx(username, checkNum + "", 180, TimeUnit.SECONDS);
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "短信发送成功，请注意接收!");
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	public String realCheck(Map<String, Object> map) {
		
		JSONObject newJsonObject = new JSONObject();
		
		String username = map.get("MMA_UserName").toString();
		String checkNum = map.get("checkNum").toString();
		String redisCheckNum = redisUtil.get(username);
		if (redisCheckNum == null || "null".equals(redisCheckNum)) {
			// 返回错误信息，验证码有失效了，过期了
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 403);
			metaJsonObject.put("msg", "验证码过期，请重试!");
			newJsonObject.put("meta", metaJsonObject);
			return newJsonObject.toString();
		}
		if (!checkNum.equals(redisCheckNum)) {
			// 返回验证码错误，请重新输入
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 404);
			metaJsonObject.put("msg", "验证码错误，请重新输入!");
			newJsonObject.put("meta", metaJsonObject);
			return newJsonObject.toString();
		}
		// 将，用户名+Useful，验证时间放入redis
		String key = username + "Useful";
		redisUtil.setEx(key, "成功验证", 3, TimeUnit.DAYS);
		// 测试用的
//		redisUtil.setEx(key, "成功验证", 30, TimeUnit.SECONDS);
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "验证成功!");
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@Transactional
	public String checkTradePassword(Map<String, Object> map) {
		
		String tradePas = map.get("tradePas").toString();
		String mma_username = map.get("mmngctUserName").toString();
		Mmngct mmngct = mmaMapper.getByUsername(mma_username);
		
		
		String msg = "";
		int status = 200;
		if (tradePas.equals(mmngct.getMMA_Trade_Password())) {
			// 密码正确
			msg = "OK";
		} else {
			// 密码错误
			msg = "交易密码错误!";
			status = 100;
		}
		
		JSONObject newJsonObject = new JSONObject();
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", status);
		metaJsonObject.put("msg", msg);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
