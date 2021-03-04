package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;


import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.CSS;
import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.FoodType;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.PSC;
import com.gxx.ordering_platform.entity.PSS;
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
	
	@Transactional
	public String pSSGoodsAndGoodstypeOptions(Map<String, Object> map) {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		List<FoodType> foodTypes = foodTypeMapper.getByFTMID(m_ID);
		
		JSONArray goodsAndGoodstypeJsonArray = new JSONArray();
		
		for (FoodType foodType : foodTypes) {
			List<Food> foods = foodMapper.getByMIDFTID(foodType.getFT_ID(), m_ID);
			JSONObject foodtypeJsonObject = new JSONObject();
			foodtypeJsonObject.put("value", foodType.getFT_ID());
			foodtypeJsonObject.put("label", foodType.getFT_Name());
			
			JSONArray foodJsonArray = new JSONArray();
			for (Food food : foods) {
				JSONObject foodJsonObject = new JSONObject();
				foodJsonObject.put("value", food.getF_ID());
				foodJsonObject.put("label", food.getF_Name());
				
				foodJsonArray.put(foodJsonObject);
			}
			foodtypeJsonObject.put("children", foodJsonArray);
			goodsAndGoodstypeJsonArray.put(foodtypeJsonObject);
		}
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("PSSGoodsAndGoodstypeOptions", goodsAndGoodstypeJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String searchPSSFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String PSSGoodID = map.get("PSSGoodID").toString();
		String PSSGoodtypeID = map.get("PSSGoodtypeID").toString();
		
		String PSSStartString = map.get("PSSStartString").toString();
		String PSSEndString = map.get("PSSEndString").toString();
		
		Integer foodId = null;
		Integer foodtypeId = null;
		
		if (!"".equals(PSSGoodID)) {
			foodId = Integer.valueOf(PSSGoodID);
		}
		if (!"".contentEquals(PSSGoodtypeID)) {
			foodtypeId = Integer.valueOf(PSSGoodtypeID);
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		
		Date PSSStartDate = format.parse(PSSStartString);
		Date PSSEndDate = format.parse(PSSEndString);
		
		List<PSS> psses = foodTypeMapper.searchPSS(m_ID, PSSStartDate, PSSEndDate, foodId, foodtypeId);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("PSSFormList", new JSONArray(psses));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchCSSFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();

		String CSSGoodtypeID = map.get("CSSGoodtypeID").toString();
		
		String CSSStartString = map.get("CSSStartString").toString();
		String CSSEndString = map.get("CSSEndString").toString();
		
		Integer foodtypeId = null;
		
		if (!"".contentEquals(CSSGoodtypeID)) {
			foodtypeId = Integer.valueOf(CSSGoodtypeID);
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		
		Date CSSStartDate = format.parse(CSSStartString);
		Date CSSEndDate = format.parse(CSSEndString);
		
		List<CSS> csses = foodTypeMapper.searchCSS(m_ID, CSSStartDate, CSSEndDate, foodtypeId);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONArray cssesJsonArray = new JSONArray(csses);
		
		int totalNum = 0;
		float totalPrice = 0f;
		
		for(int i = 0; i < csses.size(); i++) {
			totalNum += csses.get(i).getOdnum();
			totalPrice += csses.get(i).getTotalPrice();
		}
		
		for (int i = 0; i < cssesJsonArray.length(); i++) {
			cssesJsonArray.getJSONObject(i).put("numPercentage", ((float) csses.get(i).getOdnum()) / ((float) totalNum));
			cssesJsonArray.getJSONObject(i).put("pricePercentage", csses.get(i).getTotalPrice() / totalPrice);
		}
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("CSSFormList", cssesJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchPSCFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String PSCGoodID = map.get("PSCGoodID").toString();
		String PSCGoodtypeID = map.get("PSCGoodtypeID").toString();
		
		String PSCStartString = map.get("PSCStartString").toString();
		String PSCEndString = map.get("PSCEndString").toString();
		
		Integer foodId = null;
		Integer foodtypeId = null;
		
		if (!"".equals(PSCGoodID)) {
			foodId = Integer.valueOf(PSCGoodID);
		}
		if (!"".contentEquals(PSCGoodtypeID)) {
			foodtypeId = Integer.valueOf(PSCGoodtypeID);
		}
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
		
		Date PSCStartDate = format.parse(PSCStartString);
		Date PSCEndDate = format.parse(PSCEndString);
		
		List<PSC> pscs = foodTypeMapper.searchPSC(m_ID, PSCStartDate, PSCEndDate, foodId, foodtypeId);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("PSCFormList", new JSONArray(pscs));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
