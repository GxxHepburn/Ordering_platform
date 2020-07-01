package com.gxx.ordering_platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WeChatInitMenuService {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public String initMenu(String openId,String res) {
		return "meibeilanjie";
	}
}
