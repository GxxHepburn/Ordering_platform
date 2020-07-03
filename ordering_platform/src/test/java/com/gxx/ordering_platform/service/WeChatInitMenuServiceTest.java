package com.gxx.ordering_platform.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.gxx.ordering_platform.AppConfig;
import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.FoodTypeMapper;

//创建spring容器
//创建web容器
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes= {AppConfig.class})
public class WeChatInitMenuServiceTest {

	@Autowired
	FoodTypeMapper foodTypeMapper;
	
	@Autowired
	FoodMapper foodMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void initMenuTest() {
		String res = "1";
		//通过与数据库交互，获取初始化菜单所需数据
		int ft_mid = Integer.valueOf(res);
		List<FoodType> foodTypes = foodTypeMapper.getByFTMID(ft_mid);
		foodTypes.stream().forEach(food -> System.out.println(food.getFT_Name()));
	}
	
	public JSONObject getFoodsByFoodTypes(List<FoodType> foodTypes) {
		JSONObject menuJsonObject = new JSONObject();
		
		JSONArray menuJsonArray = new JSONArray();
		foodTypes.stream().forEach(foodType -> {
			JSONObject foodTypeJsonObject = new JSONObject();
			foodTypeJsonObject.put("name", foodType.getFT_Name());
			foodTypeJsonObject.put("sort", foodType.getFT_Sort());
			
			List<Food> foods = foodMapper.getByMIDANDFTID(foodType.getFT_ID(), foodType.getFT_MID());
			JSONArray foodsJsonArray = new JSONArray();
			foods.stream().forEach(food -> {
				JSONObject foodJsonObject = new JSONObject();
				foodJsonObject.put("name", food.getF_Name());
				foodJsonObject.put("image", food.getF_ImageUrl());
				foodJsonObject.put("price", food.getF_Price());
				foodJsonObject.put("status", food.getF_Status());
				foodJsonObject.put("stock", food.getF_Stock());
				foodJsonObject.put("salesVolume", food.getF_SalesVolume());
				foodJsonObject.put("tag", food.getF_tag());
				foodJsonObject.put("sort", food.getF_Sort());
				JSONArray propertyJsonArray = new JSONArray();
				//填充propertyJsonArray
				foodJsonObject.put("property", propertyJsonArray);
				JSONArray specsJsonArray = new JSONArray();
				//填充specsJsonArray
				
				foodJsonObject.put("specs", specsJsonArray);
				foodsJsonArray.put(foodJsonObject);
			});
			foodTypeJsonObject.put("foods", foodsJsonArray);
			menuJsonArray.put(foodTypeJsonObject);
		});
		menuJsonObject.put("menu", menuJsonArray);
		return menuJsonObject;
	}
}
