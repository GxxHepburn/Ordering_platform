package com.gxx.ordering_platform.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WechatOrderingService {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public String ordering(String str) {
		//获取参数
		JSONObject jsonObject = new JSONObject(str);
		String openid = jsonObject.getString("openid");
		int totalNum = jsonObject.getInt("totalNum");
		float totalPrice = jsonObject.getFloat("totalPrice");
		int mid = jsonObject.getInt("mid");
		int tid = jsonObject.getInt("tid");
		String remark = jsonObject.getString("remark");
		JSONArray ordersJsonArray = jsonObject.getJSONArray("orders");
		logger.info("openid: " + openid);
		logger.info("totalPrice: " + totalPrice);
		return "WechatOrderingService: 下单了!";
	}
}
