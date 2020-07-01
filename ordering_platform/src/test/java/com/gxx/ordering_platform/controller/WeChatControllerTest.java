package com.gxx.ordering_platform.controller;

import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeChatControllerTest {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	@Test
	public void testInitMenu() {
		String str = "{\"openid\":\"o5C-Y5KCm_mMGH2nyb8IVkxUAs50\",\"numberOfDiners\":\"11\"}";
		JSONObject jsonObject = new JSONObject(str);
		String openId = jsonObject.getString("openid");
		String numberOfDiners = jsonObject.getString("numberOfDiners");
		
		
	}
}
