package com.gxx.ordering_platform.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.gxx.ordering_platform.utils.RedisUtil;

@Controller
public class TestController {

	@Autowired
	private RedisUtil redisUtil;
	
	@GetMapping("/test")
	public String test() {
		redisUtil.set("gxx", "very good");
		System.out.println(redisUtil.get("gxx"));
		return "";
	}
}
