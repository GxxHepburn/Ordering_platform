package com.gxx.ordering_platform.service;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.mapper.WechatUserMapper;

@Component
public class OSMWechatUserService {
	
	@Autowired WechatUserMapper wechatUserMapper;
	
	@Transactional
	public String changeWechatUserStatus(Map<String, Object> map) {
		
		int U_ID = Integer.valueOf(map.get("U_ID").toString());
		int U_Status = Integer.valueOf(map.get("U_Status").toString());
		
		wechatUserMapper.changStatusByUID(U_ID, U_Status);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
