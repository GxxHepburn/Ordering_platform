package com.gxx.ordering_platform.controller;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.service.OSMAPPOrderingService;
import com.gxx.ordering_platform.service.OSMAPPTabService;

@RestController
@RequestMapping("/OSMAPP")
public class OSMAPPController {
	
	@Autowired OSMAPPTabService oSMAPPTabService;
	
	@Autowired OSMAPPOrderingService oSMAPPOrderingService;

	@PostMapping(value = "/ordersTabAndTabTypeOptions", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ordersTabAndTabTypeOptions(@RequestBody Map<String, Object> map) {
		try {
			return oSMAPPTabService.ordersTabAndTabTypeOptions(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/notTakingOrerAddFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String notTakingOrerAddFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMAPPOrderingService.notTakingOrerAddFormList(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
}
