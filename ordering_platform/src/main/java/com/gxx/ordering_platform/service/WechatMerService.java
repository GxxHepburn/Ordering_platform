package com.gxx.ordering_platform.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.mapper.MerMapper;

@Component
public class WechatMerService {

	@Autowired
	MerMapper merMapper;
	
	public String getMer(String str) {
		JSONObject jsonObject = new JSONObject(str);
		String res = jsonObject.getString("mid");
		int mid = Integer.valueOf(res);
		Mer mer = merMapper.getMerByMID(mid);
		JSONObject merJsonObject = new JSONObject(mer);
		return merJsonObject.toString();
	}
}
