package com.gxx.ordering_platform.service;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;

@Component
public class OSMAPPTabService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired TabTypeMapper tabTypeMapper;
	
	@Autowired TabMapper tabMapper;

	@Transactional
	public String ordersTabAndTabTypeOptions(Map<String, Object> map) {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		List<TabType> tabTypes = tabTypeMapper.getByMID(m_ID);
		
		JSONArray tabAndTabTypeJsonArray = new JSONArray();
		for (TabType tabType : tabTypes) {
			List<Tab> tabs = tabMapper.getByTTID(tabType.getTT_ID());
			JSONObject tabTypeJsonObject = new JSONObject();
			tabTypeJsonObject.put("value", tabType.getTT_ID());
			tabTypeJsonObject.put("text", tabType.getTT_Name());
			
			JSONArray tabJsonArray = new JSONArray();
			for (Tab tab : tabs) {
				JSONObject tabJsonObject = new JSONObject();
				tabJsonObject.put("id", tab.getT_ID());
				tabJsonObject.put("text", tab.getT_Name());
				
				tabJsonArray.put(tabJsonObject);
			}
			
			tabTypeJsonObject.put("children", tabJsonArray);
			
			tabAndTabTypeJsonArray.put(tabTypeJsonObject);
		}
		
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("ordersTabAndTabTypeOptions", tabAndTabTypeJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
