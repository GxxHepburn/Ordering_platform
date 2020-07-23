package com.gxx.ordering_platform.service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.wxPaySDK.MerchantWXPayConfig;
import com.gxx.ordering_platform.wxPaySDK.ServiceWXPayConfig;
import com.gxx.ordering_platform.wxPaySDK.WXPay;
import com.gxx.ordering_platform.wxPaySDK.WXPayConfig;
import com.gxx.ordering_platform.wxPaySDK.WXPayConstants;
import com.gxx.ordering_platform.wxPaySDK.WXPayUtil;

@Component
public class WxPayService {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${merchantNumber.body}")
	String body;
	
	@Value("${wechat.appId}")
	String appId;
	
	@Value("${merchantNumber.mchKey}")
	String mchKey;
	
	//---------------------------------------------------
	
	@Value("${serviceNumber.appid}")
	String service_appid;
	
	@Value("${serviceNumber.mch_id}")
	String service_mch_id;
	
	@Value("${serviceNumber.sub_appid}")
	String service_sub_appid;
	
	@Value("${serviceNumber.sub_mch_id}")
	String service_sub_mch_id;
	
	@Value("${serviceNumber.mchKey}")
	String ServiceMchKey;
	
	
	@Autowired
	ServiceWXPayConfig serviceWXPayConfig;
	
	
	@Autowired
	MerchantWXPayConfig merchantWXPayConfig;
	
	public Map<String, String> wxPay(String openId, String ipAddress) throws Exception {
		//1.拼接统一下单地址参数
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("body", this.body + "-test");
		paraMap.put("openid", openId);
		paraMap.put("out_trade_no", UUID.randomUUID().toString().replaceAll("-", ""));//订单号，每次都不相同。这个地方要研究研究
//================================================================
		paraMap.put("spbill_create_ip", ipAddress);
		paraMap.put("total_fee", "1");
		paraMap.put("trade_type", "JSAPI");
		logger.info("paraMap: " + paraMap);
		
		final String SUCCESS_NOTIFY = "http://www.donghuastar.com/wxpay/success";
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(merchantWXPayConfig, SUCCESS_NOTIFY, false, useSandbox);
		
		Map<String, String> map = wxPay.unifiedOrder(wxPay.fillRequestData(paraMap), 15000, 15000);
		
		String prePayId = (String) map.get("prepay_id");
		logger.info("xmlStr为: " + map);
		
		
		Map<String, String> payMap = new HashMap<String, String>();
		payMap.put("appId", this.appId);
		payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
		payMap.put("nonceStr", WXPayUtil.generateNonceStr());
		if (useSandbox) {
			payMap.put("signType", WXPayConstants.MD5);
		} else {
			payMap.put("signType", WXPayConstants.HMACSHA256);
		}
		payMap.put("package", "prepay_id=" + prePayId);
		
		String paySign = null;
		if (useSandbox) {
			paySign = WXPayUtil.generateSignature(payMap, this.mchKey, WXPayConstants.SignType.MD5);
		} else {
			paySign = WXPayUtil.generateSignature(payMap, this.mchKey, WXPayConstants.SignType.HMACSHA256);
		}
		payMap.put("paySign", paySign);
		
		logger.info("payMap: " + payMap);
		return payMap;
	}
	
	public Map<String, String> wxServicePay(String openId, String ipAddress) throws Exception {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("sub_openid", openId);
		paraMap.put("body", "郭利" + "-test");
		paraMap.put("out_trade_no", UUID.randomUUID().toString().replaceAll("-", ""));
		paraMap.put("spbill_create_ip", ipAddress);
		paraMap.put("total_fee", "1");
		paraMap.put("trade_type", "JSAPI");
		paraMap.put("sub_appid", this.service_sub_appid);
		paraMap.put("sub_mch_id", this.service_sub_mch_id);
		logger.info("paraMap: " + paraMap);
		
		final String SUCCESS_NOTIFY = "http://www.donghuastar.com/wxpay/success";
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(serviceWXPayConfig , SUCCESS_NOTIFY, false, useSandbox);
		
		Map<String, String> map = wxPay.unifiedOrder(wxPay.fillRequestData(paraMap), 15000, 15000);
		
		String prePayId = (String) map.get("prepay_id");
		logger.info("xmlStr为： " + map);
		
		Map<String, String> payMap = new HashMap<String, String>();
		payMap.put("appId", this.appId);
//		payMap.put("sub_appid", this.service_sub_appid);
//		payMap.put("sub_mch_id", this.service_sub_mch_id);
		
		payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
		payMap.put("nonceStr", WXPayUtil.generateNonceStr());
		payMap.put("signType", WXPayConstants.HMACSHA256);
		payMap.put("package", "prepay_id=" + prePayId);
		
		String paySign = null;
		logger.info(this.ServiceMchKey);
		paySign = WXPayUtil.generateSignature(payMap, this.ServiceMchKey, WXPayConstants.SignType.HMACSHA256);
		payMap.put("paySign", paySign);
		
		logger.info("payMap: " + payMap);
		return payMap;
	}
}
