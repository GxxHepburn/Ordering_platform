package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Multi_Orders_Tab;
import com.gxx.ordering_platform.mapper.OrdersMapper;

@Component
public class OSMOrderingService {
	
	@Autowired OrdersMapper ordersMapper;

	public String userOrderList(int U_ID) {
		
		//获取该用户订单
		List<Multi_Orders_Tab> multi_Orders_Tabs = ordersMapper.getOrdersByUIDOrderByIimeDESC(U_ID);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONArray ordersJsonArray = new JSONArray();
		for (Multi_Orders_Tab multi_Orders_Tab : multi_Orders_Tabs) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("O_ID", multi_Orders_Tab.getO_ID());
			jsonObject.put("O_MID", multi_Orders_Tab.getO_MID());
			jsonObject.put("O_UID", multi_Orders_Tab.getO_UID());
			jsonObject.put("O_TID", multi_Orders_Tab.getO_TID());
			jsonObject.put("O_TotlePrice", multi_Orders_Tab.getO_TotlePrice());
			jsonObject.put("O_PayStatue", multi_Orders_Tab.getO_PayStatue());
			
			//格式化时间
			jsonObject.put("O_OrderingTime", simpleDateFormat.format(multi_Orders_Tab.getO_OrderingTime()));
			if (multi_Orders_Tab.getO_PayTime() == null) {
				jsonObject.put("O_PayTime", "");
				jsonObject.put("O_OutTradeNo", "");
			} else {
				jsonObject.put("O_PayTime", simpleDateFormat.format(multi_Orders_Tab.getO_PayTime()));
				jsonObject.put("O_OutTradeNo", multi_Orders_Tab.getO_OutTradeNo());
			}
			
			jsonObject.put("O_Remarks", multi_Orders_Tab.getO_Remarks());
			jsonObject.put("O_TotleNum", multi_Orders_Tab.getO_TotleNum());
			jsonObject.put("O_UniqSearchID", multi_Orders_Tab.getO_UniqSearchID());
			
			jsonObject.put("T_Name", multi_Orders_Tab.getT_Name());
			
			ordersJsonArray.put(jsonObject);
		}
		
		newJsonObject.put("data", ordersJsonArray);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
