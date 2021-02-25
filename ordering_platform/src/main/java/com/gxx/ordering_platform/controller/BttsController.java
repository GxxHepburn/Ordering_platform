package com.gxx.ordering_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gxx.ordering_platform.service.BttsService;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
public class BttsController {
	
	@Autowired
	BttsService bttsService;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostMapping(value = "/bttsToken", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String bttsToken(@RequestBody Map<String, Object> map) {
		try {
			return bttsService.bttsToken(map);
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
