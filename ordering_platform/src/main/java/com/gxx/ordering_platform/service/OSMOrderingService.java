package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.mapper.OrdersMapper;

@Component
public class OSMOrderingService {
	
	@Autowired OrdersMapper ordersMapper;

	public String userOrderList(int U_ID) {
		
		//获取该用户订单
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUIDOrderByIimeDESC(U_ID);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONArray ordersJsonArray = new JSONArray();
		for (Multi_Orders_Tab_Tabtype multi_Orders_Tab_Tabtype : multi_Orders_Tab_Tabtypes) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("O_ID", multi_Orders_Tab_Tabtype.getO_ID());
			jsonObject.put("O_MID", multi_Orders_Tab_Tabtype.getO_MID());
			jsonObject.put("O_UID", multi_Orders_Tab_Tabtype.getO_UID());
			jsonObject.put("O_TID", multi_Orders_Tab_Tabtype.getO_TID());
			jsonObject.put("O_TotlePrice", multi_Orders_Tab_Tabtype.getO_TotlePrice());
			jsonObject.put("O_PayStatue", multi_Orders_Tab_Tabtype.getO_PayStatue());
			
			//格式化时间
			jsonObject.put("O_OrderingTime", simpleDateFormat.format(multi_Orders_Tab_Tabtype.getO_OrderingTime()));
			if (multi_Orders_Tab_Tabtype.getO_PayTime() == null) {
				jsonObject.put("O_PayTime", "");
				jsonObject.put("O_OutTradeNo", "");
			} else {
				jsonObject.put("O_PayTime", simpleDateFormat.format(multi_Orders_Tab_Tabtype.getO_PayTime()));
				jsonObject.put("O_OutTradeNo", multi_Orders_Tab_Tabtype.getO_OutTradeNo());
			}
			
			jsonObject.put("O_Remarks", multi_Orders_Tab_Tabtype.getO_Remarks());
			jsonObject.put("O_TotleNum", multi_Orders_Tab_Tabtype.getO_TotleNum());
			jsonObject.put("O_UniqSearchID", multi_Orders_Tab_Tabtype.getO_UniqSearchID());
			
			String T_Name = multi_Orders_Tab_Tabtype.getT_Name();
			if (T_Name == null) {
				T_Name = "餐桌已删除";
			}
			jsonObject.put("T_Name", T_Name);
			String TT_Name = multi_Orders_Tab_Tabtype.getTT_Name();
			if (TT_Name == null) {
				TT_Name = "分类已删除";
			}
			jsonObject.put("TT_Name", TT_Name);
			
			ordersJsonArray.put(jsonObject);
		}
		
		newJsonObject.put("data", ordersJsonArray);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
