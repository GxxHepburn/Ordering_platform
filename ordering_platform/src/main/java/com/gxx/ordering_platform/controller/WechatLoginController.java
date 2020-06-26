package com.gxx.ordering_platform.controller;

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
		try {
			return wechatLoginService.singin(APPID, APPSECRET, wechatCode.code);
		} catch (Exception e) {
			//登陆验证失败
			return "0";
		}
	}
}
