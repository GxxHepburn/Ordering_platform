package com.gxx.ordering_platform.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.AppConfig;
import com.gxx.ordering_platform.entity.WechatCode;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.service.WeChatInitMenuService;
import com.gxx.ordering_platform.service.WechatLoginService;
import com.gxx.ordering_platform.service.WechatOrderingService;
import com.gxx.ordering_platform.service.WechatTableService;

@RestController
@RequestMapping("/wechat")
public class WechatController {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	WechatLoginService wechatLoginService;
	@Autowired
	WeChatInitMenuService weChatInitMenuService;
	
	@Autowired
	WechatTableService wechatTableService;
	
	@Autowired
	WechatOrderingService wechatOrderingService;
	
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
			//没有这个用户，那么就初始化这个用户
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
	
	@PostMapping(value = "/loggedIn/initMenu",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String initMenu(@RequestBody String str) {
		JSONObject jsonObject = new JSONObject(str);
		String res = jsonObject.getString("res");
		return weChatInitMenuService.initMenu(res);
	}
	
//	@PostMapping("/xx")
//	public String test(HttpServletResponse response) {
//		System.out.println(response.getCharacterEncoding());
//		return "x";
//	}
	
	@PostMapping(value = "/loggedIn/getTabNameAndTabTypeName",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getTabNameAndTabTypeName(@RequestBody String str) {
		JSONObject jsonObject = new JSONObject(str);
		String tableId = jsonObject.getString("table");
		return wechatTableService.getTabNameAndTabTypeName(tableId);
	}
	
	
	@PostMapping(value = "/loggedIn/order",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String order(@RequestBody String str) {
		try {
			wechatOrderingService.ordering(str);
		} catch (Exception e) {
			//遇到错误，返回下单失败
			return "0";
		}
		//下单成功
		return "1";
	}
}
