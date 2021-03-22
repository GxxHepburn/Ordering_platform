package com.gxx.ordering_platform;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) throws Exception {
		
		new Test().test();
	}
	
	void test () {
		try {
			throw new RuntimeException();
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
	}
}
