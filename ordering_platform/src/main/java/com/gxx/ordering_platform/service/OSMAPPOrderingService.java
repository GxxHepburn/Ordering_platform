package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_OrderAdd_Tab_Tabtype_Orders;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;

@Component
public class OSMAPPOrderingService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired OrderAddMapper orderAddMapper;

	@Transactional
	public String notTakingOrerAddFormList (Map<String, Object> map) {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		Integer tabId = null;
		if (!"".equals(map.get("totalTabId").toString())) {
			tabId = Integer.valueOf(map.get("totalTabId").toString());
		}
		
		
		List<Multi_OrderAdd_Tab_Tabtype_Orders> multi_OrderAdd_Tab_Tabtypes_Orderses = orderAddMapper.getNotTakingByMIDTabIdOrderByOrderingTime(m_ID, tabId, limitStart, pagesizeInt);
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取未接单列表成功!");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray notTakingOrerAddJsonArray = new JSONArray();
		
		for (int i = 0; i < multi_OrderAdd_Tab_Tabtypes_Orderses.size(); i++) {
			JSONObject notTakingOrerAddJSONObject = new JSONObject(multi_OrderAdd_Tab_Tabtypes_Orderses.get(i));
			notTakingOrerAddJSONObject.put("OA_OrderingTime", simpleDateFormat.format(multi_OrderAdd_Tab_Tabtypes_Orderses.get(i).getOA_OrderingTime()));
			if (multi_OrderAdd_Tab_Tabtypes_Orderses.get(i).getO_PayTime() == null) {
				notTakingOrerAddJSONObject.put("o_PayTime", "");
			} else {
				notTakingOrerAddJSONObject.put("o_PayTime", simpleDateFormat.format(multi_OrderAdd_Tab_Tabtypes_Orderses.get(i).getO_PayTime()));
			}
			
			notTakingOrerAddJsonArray.put(notTakingOrerAddJSONObject);
		}
		
		
		dataJsonObject.put("notTakingOrerAddFormList", notTakingOrerAddJsonArray);
		
		newJsonObject.put("meta", metaJsonObject);
		newJsonObject.put("data", dataJsonObject);
		
		return newJsonObject.toString();
	}
}
