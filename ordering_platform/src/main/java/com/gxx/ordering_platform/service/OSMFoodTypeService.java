package com.gxx.ordering_platform.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.FoodPropertyMapper;
import com.gxx.ordering_platform.mapper.FoodSpecificationsMapper;
import com.gxx.ordering_platform.mapper.FoodTypeMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;

@Component
public class OSMFoodTypeService {
	
	@Autowired FoodTypeMapper foodTypeMapper;
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired FoodSpecificationsMapper foodSpecificationsMapper;
	
	@Autowired FoodPropertyMapper foodPropertyMapper;
	
	@Autowired FoodMapper foodMapper;

	@Transactional
	public String cates(Map<String, Object> map) {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		List<FoodType> foodTypes = foodTypeMapper.getByFTMID(m_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONArray foodTypesJsonArray = new JSONArray(foodTypes);
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("cates", foodTypesJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchCates(Map<String, Object> map) {
		
		String query = (String) map.get("query");
		int pagenumInt = (int) map.get("pagenum");
		int pagesizeInt = (int) map.get("pagesize");
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		List<FoodType> foodTypes = foodTypeMapper.getByFTMIDWithQuery(m_ID, limitStart, pagesizeInt, query);
		int catesTotal = foodTypeMapper.getTotalByFTMIDWithQuery(m_ID, query);
		
		JSONArray foodTypesJsonArray = new JSONArray();
		
		for (FoodType foodType : foodTypes) {
			JSONObject foodTypeJsonObject = new JSONObject(foodType);
			
			Food food = foodMapper.isCateNullByFTID(foodType.getFT_ID());
			if (food == null) {
				foodTypeJsonObject.put("IsCateNull", true);
			} else {
				foodTypeJsonObject.put("IsCateNull", false);
			}
			
			foodTypesJsonArray.put(foodTypeJsonObject);
		}
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("cates", foodTypesJsonArray);
		dataJsonObject.put("total", catesTotal);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String deleteCate(Map<String, Object> map) {
		
		int FT_ID = Integer.valueOf(map.get("FT_ID").toString());
		
		foodTypeMapper.deleteByFTID(FT_ID);
		
		foodMapper.deleteFoodByFID(FT_ID);
		foodSpecificationsMapper.deleteByFTID(FT_ID);
		foodPropertyMapper.deleteByFTID(FT_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "删除成功");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@Transactional
	public String changeFTName(Map<String, Object> map) {
		
		int FT_ID = Integer.valueOf(map.get("FT_ID").toString());
		String newFTName = map.get("copyFTName").toString();
		
		foodTypeMapper.updateFTNameByFTID(FT_ID, newFTName);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "修改分类名称成功");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@Transactional
	public String addFT(Map<String, Object> map) {
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String FT_Name = map.get("FT_Name").toString();
		
		foodTypeMapper.insert(m_ID, FT_Name);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "添加成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
