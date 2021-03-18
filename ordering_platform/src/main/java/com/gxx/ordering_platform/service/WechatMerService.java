package com.gxx.ordering_platform.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.TabMapper;

@Component
public class WechatMerService {

	@Autowired
	MerMapper merMapper;
	
	@Autowired
	TabMapper tabMapper;
	
	public String getMer(String str) {
		JSONObject jsonObject = new JSONObject(str);
		String res = jsonObject.getString("mid");
		String table = jsonObject.getString("tabId");
		int mid = Integer.valueOf(res);
		int tabId = Integer.valueOf(table);
		Tab tab = tabMapper.getByTabId(tabId);
		if (tab == null) {
			 // tab被删除 所以不能下单
			return "0";
		} else {
			Mer mer = merMapper.getMerByMID(mid);
			JSONObject merJsonObject = new JSONObject(mer);
			merJsonObject.put("m_Sub_Mch_ID", "");
			return merJsonObject.toString();
		}
	}
}
