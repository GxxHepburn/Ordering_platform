package com.gxx.ordering_platform.service;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;

@Component
public class WechatOrderingService {
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	WechatUserMapper wechatUserMapper;

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
		Date orderingTime = new Date();
		WechatUser wechatUser = wechatUserMapper.getByUOpenId(openid);
		logger.info("orders: " + ordersJsonArray);
		
		Orders orders = new Orders();
		orders.setO_MID(mid);
		orders.setO_UID(wechatUser.getU_ID());
		orders.setO_TID(tid);
		orders.setO_TotlePrice(totalPrice);
		orders.setO_PayStatue(0);
		orders.setO_OrderingTime(orderingTime);
		orders.setO_Remarks(remark);
		orders.setO_TotleNum(totalNum);
		
		ordersMapper.insert(orders);
		logger.info("O_ID: " + orders.getO_ID());
		
		return "WechatOrderingService: 下单了!";
	}
}
