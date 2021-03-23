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
import com.gxx.ordering_platform.entity.FoodProperty;
import com.gxx.ordering_platform.entity.FoodSpecifications;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.FoodPropertyMapper;
import com.gxx.ordering_platform.mapper.FoodSpecificationsMapper;
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
	
	@Autowired
	FoodSpecificationsMapper foodSpecificationsMapper;
	
	@Autowired
	FoodPropertyMapper foodPropertyMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Test
	public void initMenuTest() {
		String res = "1";
		//通过与数据库交互，获取初始化菜单所需数据
		int ft_mid = Integer.valueOf(res);
		List<FoodType> foodTypes = foodTypeMapper.getByFTMID(ft_mid);
		System.out.println(getMenuJson(foodTypes).toString());;
	}
	
	public JSONObject getMenuJson(List<FoodType> foodTypes) {
		JSONObject menuJsonObject = new JSONObject();
		
		JSONArray menuJsonArray = new JSONArray();
		foodTypes.stream().forEach(foodType -> {
			JSONObject foodTypeJsonObject = new JSONObject();
			foodTypeJsonObject.put("name", foodType.getFT_Name());
			foodTypeJsonObject.put("sort", foodType.getFT_Sort());
			
			List<Food> foods = foodMapper.getByMIDAndFTIDWithoutDisable(foodType.getFT_ID(), foodType.getFT_MID());
			JSONArray foodsJsonArray = new JSONArray();
			foods.stream().forEach(food -> {
				JSONObject foodJsonObject = new JSONObject();
				foodJsonObject.put("name", food.getF_Name());
				foodJsonObject.put("image", food.getF_ImageUrl());
				foodJsonObject.put("price", food.getF_Price());
				foodJsonObject.put("status", food.getF_Statue());
				foodJsonObject.put("stock", food.getF_Stock());
				foodJsonObject.put("salesVolume", food.getF_SalesVolume());
				foodJsonObject.put("tag", food.getF_Tag());
				foodJsonObject.put("sort", food.getF_Sort());
				JSONArray propertyJsonArray = new JSONArray();
				//填充propertyJsonArray
				List<FoodProperty> foodProperties = foodPropertyMapper.getByMIDAndFTIDAndFID(food.getF_MID(), 
						food.getF_FTID(), food.getF_ID());
				foodProperties.stream().forEach(foodProperty -> {
					JSONObject propertyJsonObject = new JSONObject();
					propertyJsonObject.put("name", foodProperty.getFP_Name());
					propertyJsonObject.put("valueOne", foodProperty.getFP_ValueOne());		
					propertyJsonObject.put("valueTwo", foodProperty.getFP_ValueTwo());
					propertyJsonObject.put("valueThree", foodProperty.getFP_ValueThree());
					propertyJsonObject.put("valueFour", foodProperty.getFP_ValueFour());
					propertyJsonObject.put("valueFive", foodProperty.getFP_ValueFive());
					
					propertyJsonArray.put(propertyJsonObject);
				});
 
				foodJsonObject.put("property", propertyJsonArray);
				JSONArray specsJsonArray = new JSONArray();
				//填充specsJsonArray
				List<FoodSpecifications> foodSpecifications = foodSpecificationsMapper.getByMIDAndFTIDAndFID(food.getF_MID(), 
						food.getF_FTID(), food.getF_ID());
				foodSpecifications.stream().forEach(foodSpec -> {
					JSONObject specJsonObject = new JSONObject();
					specJsonObject.put("name", foodSpec.getFS_Key());
					specJsonObject.put("value", foodSpec.getFS_Value());

					specsJsonArray.put(specJsonObject);
				});
				
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
