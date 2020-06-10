package com.gxx.ordering_platform.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class TestNgnixController {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	@GetMapping("/ngnix")
	public ModelAndView index() {
		logger.info("ngnix proxy");
		return null;
	}
}
