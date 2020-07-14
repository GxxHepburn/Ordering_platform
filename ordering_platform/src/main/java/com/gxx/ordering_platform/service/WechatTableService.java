package com.gxx.ordering_platform.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;

@Component
public class WechatTableService {

	@Autowired
	TabMapper tabMapper;
	
	@Autowired
	TabTypeMapper tabTypeMapper;
	
	public String getTabNameAndTabTypeName(String tableId) {
		int t_id = Integer.parseInt(tableId);
		Tab tab = tabMapper.getByTabId(t_id);
		TabType tabType = tabTypeMapper.getByTabTypeId(tab.getT_TTID());
		JSONObject tabAndTabTypeNameJsonObject = new JSONObject();
		tabAndTabTypeNameJsonObject.put("tableName", tab.getT_Name());
		tabAndTabTypeNameJsonObject.put("tabTypeName", tabType.getTT_Name());
		return tabAndTabTypeNameJsonObject.toString();
	}
}
