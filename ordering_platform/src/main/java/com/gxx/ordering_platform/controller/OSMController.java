package com.gxx.ordering_platform.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.utils.JWTUtils;

@RestController
@RequestMapping("/OSM")
public class OSMController {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostMapping("/login")
	@ResponseBody
	public String login() {
		// 测试，直接通过，同时设置一个jwt
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("uid", "123456");
		String jwtString = JWTUtils.createJwt("gxx", 60000, claims);
		return jwtString;
		// 接下来要进行真的登陆行为了！
	}
}
