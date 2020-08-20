package com.gxx.ordering_platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/OSM")
public class OSMController {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostMapping("/login")
	@ResponseBody
	public String login() {
		return "";
	}
}
