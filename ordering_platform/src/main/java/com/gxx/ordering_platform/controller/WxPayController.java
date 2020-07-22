package com.gxx.ordering_platform.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.entity.WxPayNotifyV0;
import com.gxx.ordering_platform.service.WxPayService;
import com.gxx.ordering_platform.wxPaySDK.WXPayUtil;

@RestController
@RequestMapping("/wxpay")
public class WxPayController {
	
	@Autowired
	WxPayService wxPayService;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@PostMapping("/{openId}")
	@ResponseBody
	public String pay(HttpServletRequest request, @PathVariable String openId){
		logger.info("openId: " + openId);
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.indexOf(",") != -1) {
			String[] ips = ip.split(",");
			ip = ips[0].trim();
		}
		Map<String, String> payMap = null;
		try {
			payMap = wxPayService.wxPay(openId, ip);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject(payMap).toString();
	}
	
	@RequestMapping(value = "/success", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String success(HttpServletRequest request, @RequestBody WxPayNotifyV0 param) {
		logger.info("success: ");
		Map<String, String> result = new HashMap<String, String>();
		if ("SUCCESS".equals(param.getReturn_code())) {
			result.put("return_code", "SUCCESS");
			result.put("return_msg", "OK");
		}
		logger.info(String.valueOf(param));
		String successReturn = null;
		try {
			successReturn =  WXPayUtil.mapToXml(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return successReturn;
	}
}
