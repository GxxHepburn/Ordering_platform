package com.gxx.ordering_platform.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;

@Component
public class OSMOrderDetailService {

	@Autowired OrderDetailMapper orderDetailMapper;
	
	@Autowired FoodMapper foodMapper;
	
	@Transactional
	public String orderDetails(int O_ID) {
		
		List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(O_ID);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取订单详情成功");
		
		JSONArray jsonArray = new JSONArray();
		for(int i=0; i<orderDetails.size(); i++) {
			JSONObject orderDetailJsonObject = new JSONObject();
			orderDetailJsonObject.put("id", orderDetails.get(i).getOD_FID());
			Food food = foodMapper.getByFoodId(orderDetails.get(i).getOD_FID());
			orderDetailJsonObject.put("name", food.getF_Name());
			orderDetailJsonObject.put("price", orderDetails.get(i).getOD_RealPrice());
			orderDetailJsonObject.put("specs", orderDetails.get(i).getOD_Spec());
			JSONArray proJsonArray = new JSONArray();
			proJsonArray.put(orderDetails.get(i).getOD_PropOne());
			proJsonArray.put(orderDetails.get(i).getOD_PropTwo());
			orderDetailJsonObject.put("property", proJsonArray);
			orderDetailJsonObject.put("num", orderDetails.get(i).getOD_Num());
			
			jsonArray.put(orderDetailJsonObject);
		}
		
		newJsonObject.put("meta", metaJsonObject);
		newJsonObject.put("data", jsonArray);
		
		return newJsonObject.toString();
	}
}
