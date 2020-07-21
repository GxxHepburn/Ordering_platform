package com.gxx.ordering_platform.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/wxpay")
public class WxPayController {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
}
