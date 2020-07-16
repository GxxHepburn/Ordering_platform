package com.gxx.ordering_platform.service;

import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;

@Component
public class WechatOrderingService {
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	WechatUserMapper wechatUserMapper;
	
	@Autowired
	FoodMapper foodMapper;
	
	@Autowired
	OrderDetailMapper orderDetailMapper;

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Transactional
	public void ordering(String str) {
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
		
		for(int i=0; i<ordersJsonArray.length(); i++) {
			JSONObject orderDetailJsonObject = ordersJsonArray.getJSONObject(i);
			OrderDetail orderDetail = getByOrdersJsonArray(orders, orderDetailJsonObject);
			//获取现在真是的库存
			logger.info("OD_FID: " + orderDetail.getOD_FID());
			int nowStock = foodMapper.getStockByFID(orderDetail.getOD_FID());
			int realNum = 0;
			int overSellNum = 0;
			int D_value = 0;
			if (nowStock < 0) {
				realNum = orderDetail.getOD_Num();
				//不需要更新库存
			} else {
				D_value = nowStock - orderDetail.getOD_Num();
				if (D_value < 0) {
					//触发超卖警告
					//TD
					//将库存设置为0
					overSellNum = -D_value;
					D_value = 0;
					//设置orderDetail-realNum
					realNum = nowStock;
				} else {
					realNum = orderDetail.getOD_Num();
				}
				//设置菜品库存D_value
				foodMapper.updateStockByFID(D_value, orderDetail.getOD_FID());
			}
			orderDetail.setOD_RealNum(realNum);
			orderDetailMapper.insert(orderDetail);
			logger.info("OD_ID: " + orderDetail.getOD_ID());
		}
	}
	
	//根据JSONObject转化为OrderDetail对象
	private OrderDetail getByOrdersJsonArray(Orders orders, JSONObject orderDetailJsonObject) {
		OrderDetail orderDetail = new OrderDetail();
		int OD_OID = orders.getO_ID();
		int OD_FID = orderDetailJsonObject.getInt("id");
		int OD_FoodState = 0;
		float OD_RealPrice = orderDetailJsonObject.getFloat("price");
		String OD_Spec = orderDetailJsonObject.getString("specs");
		String OD_PropOne = "";
		String OD_PropTwo = "";
		JSONArray propJsonArray = orderDetailJsonObject.getJSONArray("property");
		for(int i=0; i<propJsonArray.length(); i++) {
			if (i==0) {
				OD_PropOne = propJsonArray.getString(i);
			}
			if (i==1) {
				OD_PropTwo = propJsonArray.getString(i);
			}
		}
		int OD_Num = orderDetailJsonObject.getInt("num");
		
		orderDetail.setOD_OID(OD_OID);
		orderDetail.setOD_FID(OD_FID);
		orderDetail.setOD_FoodState(OD_FoodState);
		orderDetail.setOD_RealPrice(OD_RealPrice);
		orderDetail.setOD_Spec(OD_Spec);
		orderDetail.setOD_PropOne(OD_PropOne);
		orderDetail.setOD_PropTwo(OD_PropTwo);
		orderDetail.setOD_Num(OD_Num);
		
		return orderDetail;
	}
}
