package com.gxx.ordering_platform.controller;

import java.util.Date;
import java.util.Map;

import org.json.JSONException;
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
import org.springframework.web.context.WebApplicationContext;

import com.gxx.ordering_platform.AppConfig;
import com.gxx.ordering_platform.entity.QrCode;
import com.gxx.ordering_platform.entity.WechatCode;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.QrCodeMapper;
import com.gxx.ordering_platform.service.QrCodeService;
import com.gxx.ordering_platform.service.WeChatInitMenuService;
import com.gxx.ordering_platform.service.WechatLoginService;
import com.gxx.ordering_platform.service.WechatMerService;
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
	
	@Autowired
	QrCodeService qrCodeService;
	
	@Autowired
	WechatMerService wechatMerService;
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Transactional
	@PostMapping("/login")
	@ResponseBody
	public String login(@RequestBody WechatCode wechatCode) {
		final String APPID = AppConfig.APPID;
		final String APPSECRET = AppConfig.APPSECRET;
		
		
		String UOPENID = null;
		// 微信小程序获取用户在本小程序中唯一的openid
		try {
			UOPENID =  wechatLoginService.singin(APPID, APPSECRET, wechatCode.code);
		} catch (Exception e) {
			//登陆验证失败
			return "0";
		}
		logger.info("/login_openid: " + UOPENID);
		
		// 我们自己系统中业务，与腾讯无关
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
			// 查看用户U_Status是否为0，如果为0，则返回0，小程序不检查是，因为持有的openid为0，小程序什么也做不了
			if (wechatUser.getU_Status() == 0) {
				return "0";
			}
		}
		return UOPENID;
	}
	
	@PostMapping(value = "/loggedIn/selectQrCode",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String selectQrCode(@RequestBody String str) {
		JSONObject jsonObject = new JSONObject(str);
		String url = jsonObject.getString("QrCode");
		return qrCodeService.checking(url);
	}
	
	
	@PostMapping(value = "/loggedIn/initMenu",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String initMenu(@RequestBody String str) {
		JSONObject jsonObject = new JSONObject(str);
		String res = jsonObject.getString("res");
		return weChatInitMenuService.initMenu(res).toString();
	}

	
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
		JSONObject jsonObject = new JSONObject(str);
		int mid = jsonObject.getInt("mid");
		logger.info("wechatController_mid: " + mid);
		String orderSearchId = "";
		try {
			orderSearchId = wechatOrderingService.ordering(str);
			// 对结果进行判断,如果meta=410,直接返回
			
			try {
				// 如果没超卖了，那么new JSOBObject就会报错
				new JSONObject(orderSearchId);
			} catch (Exception e) {
				//下单失败-更新客户端menu
				WeChatInitMenuService weChatInitMenuService = (WeChatInitMenuService)webApplicationContext.getBean("weChatInitMenuService");
				
				String newJsonStr = weChatInitMenuService.initMenu(String.valueOf(mid)).toString();
				JSONObject newJsonObject = new JSONObject(newJsonStr);
				newJsonObject.put("orderSearchId", orderSearchId);
				return newJsonObject.toString();
			}
			// 如果没报错，那么说明超卖了，直接return
			return orderSearchId;
		} catch (Exception e) {
			//遇到错误，返回下单失败
			logger.error(e.toString());
			e.printStackTrace();
			return "0";
		}
	}
	
	@PostMapping(value = "/loggedIn/add",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String add(@RequestBody String str) {
		JSONObject jsonObject = new JSONObject(str);
		int mid = jsonObject.getInt("mid");
		String addReturnString = "";
		//加菜逻辑
		try {
			addReturnString = wechatOrderingService.add(str);
			try {
				new JSONObject(addReturnString);
			} catch (Exception e) {
				// TODO: handle exception
				//下单成功-更新客户端menu
				WeChatInitMenuService weChatInitMenuService = (WeChatInitMenuService)webApplicationContext.getBean("weChatInitMenuService");
				return weChatInitMenuService.initMenu(String.valueOf(mid)).toString();
			}
			return addReturnString;
		} catch (Exception e) {
			//遇到错误，返回夹菜失败
			e.printStackTrace();
			logger.error(e.toString());
			return "0";
		}
		
	}
	
	@PostMapping(value = "/loggedIn/home",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String home(@RequestBody String str) {
		String returnStr = null;
		try {
			returnStr = wechatOrderingService.home(str);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error(e.toString());
			return "0";
		}
		return returnStr;
	}
	
	@PostMapping(value = "/loggedIn/touchDetail",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String touchDetail(@RequestBody String str) {
		String returnStr = null;
		try {
			returnStr = wechatOrderingService.touchDetail(str);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
			return "0";
		}
		return returnStr;
	}
	
	@PostMapping(value = "/loggedIn/getMer",produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getMer(@RequestBody String str) {
		return wechatMerService.getMer(str);
	}
	
	@PostMapping(value = "/loggedIn/onReachBottom", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String onReachBottom(@RequestBody Map<String, Object> map) {
		try {
			return wechatOrderingService.onReachBottom(map);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "失败!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
}
