package com.gxx.ordering_platform.service;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.FoodProperty;
import com.gxx.ordering_platform.entity.FoodSpecifications;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.FoodPropertyMapper;
import com.gxx.ordering_platform.mapper.FoodSpecificationsMapper;
import com.gxx.ordering_platform.mapper.FoodTypeMapper;

@Component
public class WeChatInitMenuService {
	
	@Autowired
	FoodTypeMapper foodTypeMapper;
	
	@Autowired
	FoodMapper foodMapper;
	
	@Autowired
	FoodSpecificationsMapper foodSpecificationsMapper;
	
	@Autowired
	FoodPropertyMapper foodPropertyMapper;

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	//事物-防止读取菜单过程中，被修改库存
	@Transactional
	public String initMenu(String res) {
		int ft_mid = Integer.valueOf(res);
		List<FoodType> foodTypes = foodTypeMapper.getByFTMID(ft_mid);
		return getMenuJson(foodTypes).toString();
	}
	
	public JSONObject getMenuJson(List<FoodType> foodTypes) {
		JSONObject menuJsonObject = new JSONObject();
		
		JSONArray menuJsonArray = new JSONArray();
		foodTypes.stream().forEach(foodType -> {
			JSONObject foodTypeJsonObject = new JSONObject();
			foodTypeJsonObject.put("name", foodType.getFT_Name());
			foodTypeJsonObject.put("sort", foodType.getFT_Sort());
			foodTypeJsonObject.put("id", foodType.getFT_ID());
			
			List<Food> foods = foodMapper.getByMIDAndFTID(foodType.getFT_ID(), foodType.getFT_MID());
			JSONArray foodsJsonArray = new JSONArray();
			foods.stream().forEach(food -> {
				JSONObject foodJsonObject = new JSONObject();
				foodJsonObject.put("id", food.getF_ID());
				foodJsonObject.put("name", food.getF_Name());
				foodJsonObject.put("image", food.getF_ImageUrl());
				foodJsonObject.put("price", food.getF_Price());
				foodJsonObject.put("status", food.getF_Status());
				foodJsonObject.put("unit", food.getF_Unit());
				foodJsonObject.put("stock", food.getF_Stock());
				foodJsonObject.put("salesVolume", food.getF_SalesVolume());
				foodJsonObject.put("tag", food.getF_tag());
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
