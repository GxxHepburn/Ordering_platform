package com.gxx.ordering_platform.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Version;
import java.time.Duration;
import java.util.Date;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.WechatUserMapper;


@Component
public class WechatLoginService {
	
	@Autowired
	WechatUserMapper wechatUserMapper;

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	static HttpClient httpClient = HttpClient.newBuilder().build();
	
	//获取wechat_openId
	public String singin(String appId, String appSecret, String code) throws Exception {
		String url = "https://api.weixin.qq.com/sns/jscode2session?"
				+ "appid=" + appId 
				+ "&secret=" + appSecret
				+ "&js_code=" + code
				+ "&grant_type=authorization_code";
		
		logger.info("code2sessionUrl: " + url);
		logger.info("wechatCode: " + code);
		
		HttpRequest request = HttpRequest.newBuilder(new URI(url))
				.timeout(Duration.ofSeconds(5))
				.version(Version.HTTP_1_1).build();
		HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
		InputStream inputStream = response.body();
		Reader reader = new InputStreamReader(inputStream, "UTF-8");
		char[] jscode2sessionCharArray = new char[1024];
		reader.read(jscode2sessionCharArray);
		String code2sessionString = String.valueOf(jscode2sessionCharArray);
		logger.info("code2sessionString: " + String.valueOf(jscode2sessionCharArray));
		JSONObject code2sessionObject = new JSONObject(code2sessionString);
		
		
		String OPENID = null;
		String SESSION_KEY = null;
		OPENID = code2sessionObject.getString("openid");
		SESSION_KEY = code2sessionObject.getString("session_key");
		logger.info("openId: " + OPENID);
		logger.info("session_key: " + SESSION_KEY);
		return OPENID;
	}
	
	//查看是否存在该用户
	public WechatUser getUserByUOpenId(String uopenid) {
		return wechatUserMapper.getByUOpenId(uopenid);
	}
	//存在则修改登陆时间
	public boolean updateWechatUserByUOpenId(WechatUser wechatUser) {
		return wechatUserMapper.updateLoginTimeByOpenId(wechatUser);
	}
	//不存在则初始化该用户
	//@Transactional
	public boolean insertWechatNoUID(WechatUser wechatUser) {
		return wechatUserMapper.insert(wechatUser);
	}
}
