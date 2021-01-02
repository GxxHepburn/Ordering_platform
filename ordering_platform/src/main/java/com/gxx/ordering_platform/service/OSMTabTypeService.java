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
public class OSMTabTypeService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired TabTypeMapper tabTypeMapper;
	
	@Autowired TabMapper tabMapper;

	@Transactional
	public String tabtypes(Map<String, Object> map) {
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		List<TabType> tabTypesList = tabTypeMapper.getByMID(m_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray tabtypesArray = new JSONArray(tabTypesList);
		dataJsonObject.put("tabtypes", tabtypesArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchTabTypes (Map<String, Object> map) {
		
		String query = (String) map.get("query");
		int pagenumInt = (int) map.get("pagenum");
		int pagesizeInt = (int) map.get("pagesize");
		String mmngctUserName = map.get("mmngctUserName").toString();
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		List<TabType> tabTypesList = tabTypeMapper.getByMIDWithQuery(m_ID, limitStart, pagesizeInt, query);
		int tabTypesTotal = tabTypeMapper.getTotalByFTMIDWithQuery(m_ID, query);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray tabtypesArray = new JSONArray();
		
		for (TabType tabType : tabTypesList) {
			JSONObject tabTypeJsonObject = new JSONObject(tabType);
			
			Tab tab = tabMapper.isTabTypeNullByTTID(tabType.getTT_ID());
			
			if (tab == null) {
				tabTypeJsonObject.put("IsTabTypeNull", true);
			} else {
				tabTypeJsonObject.put("IsTabTypeNull", false);
			}
			tabtypesArray.put(tabTypeJsonObject);
		}
		dataJsonObject.put("tabtypes", tabtypesArray);
		dataJsonObject.put("total", tabTypesTotal);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String deleteTabType (Map<String, Object> map) {
		int TT_ID = Integer.valueOf(map.get("TT_ID").toString());
		
		tabTypeMapper.deleteByTTID(TT_ID);
		
		tabMapper.deleteByTTID(TT_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "删除成功");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@Transactional
	public String changeTTName (Map<String, Object> map) {
		
		int TT_ID = Integer.valueOf(map.get("TT_ID").toString());
		String newTTName = map.get("copyTTName").toString();
		
		tabTypeMapper.updateTTNameByTTID(TT_ID, newTTName);
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "修改分类名称成功");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@Transactional
	public String addTT (Map<String, Object> map) {
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String TT_Name = map.get("TT_Name").toString();
		
		tabTypeMapper.insert(m_ID, TT_Name);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "添加成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
