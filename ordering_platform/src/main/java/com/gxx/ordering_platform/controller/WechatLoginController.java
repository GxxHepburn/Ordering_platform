package com.gxx.ordering_platform.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.AppConfig;
import com.gxx.ordering_platform.entity.WechatCode;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.service.WechatLoginService;

@RestController
@RequestMapping("/wechat")
public class WechatLoginController {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	WechatLoginService wechatLoginService;
	
	@PostMapping("/login")
	@ResponseBody
	public String login(@RequestBody WechatCode wechatCode) {
		final String APPID = AppConfig.APPID;
		final String APPSECRET = AppConfig.APPSECRET;
		
		String UOPENID = null;
		try {
			UOPENID =  wechatLoginService.singin(APPID, APPSECRET, wechatCode.code);
		} catch (Exception e) {
			//登陆验证失败
			return "0";
		}
		//看看用户是否存在，若不存在，则初始化用户表，如果存在就更新登陆时间
		
		WechatUser wechatUser = wechatLoginService.getUserByUOpenId(UOPENID);
//		logger.info("wechatUser: " + wechatUser);
		Date date = new Date();
		if (wechatUser == null) {
			//没有这个用户，那么酒初始化这个用户
			WechatUser wechatUserInsert = new WechatUser();
			wechatUserInsert.setU_LoginTime(date);
			wechatUserInsert.setU_RegisterTime(date);
			wechatUserInsert.setU_OpenId(UOPENID);
			boolean insertStatus = wechatLoginService.insertWechatNoUID(wechatUserInsert);
			if (insertStatus == false) {
				logger.error("WechatLoginController_WechatLoginService_insertWechatNoUID: " 
						+ "初始化Wechat用户失败");
			}
		} else {
			//update loginTime
			wechatUser.setU_LoginTime(date);
			boolean updateStatus = wechatLoginService.updateWechatUserByUOpenId(wechatUser);
			if (updateStatus == false) {
				logger.error("WechatLoginController_WechatLoginService_updateWechatUserByUOpenId: "
						+ "更新Wechat用户登陆时间失败");
			}
		}
		return UOPENID;
	}
}
