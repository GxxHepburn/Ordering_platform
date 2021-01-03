package com.gxx.ordering_platform.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.ietf.jgss.Oid;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.mapper.OrdersMapper;
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
	
	@Autowired
	OrdersMapper ordersMapper;
	
	public void updateIsPay(String searchId, int isPay) {
		ordersMapper.updateIsPay(searchId, isPay);
	}
	
	@Transactional
	public void updatePaied(String out_trade_no, int isPayNow, int payStatues, Date payTime) {
		ordersMapper.updatePaied(out_trade_no, isPayNow, payStatues, payTime);
	}
	
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
		
		final String SUCCESS_NOTIFY = "https://www.donghuastar.com/wxpay/success";
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
	
	// 小程序用的是这个
	public Map<String, String> wxServicePay(String openId, String ipAddress, String str) throws Exception {
		
		//要把生曾UUID放到数据库里
		//根据str得到searcId，和total_fee
		JSONObject jsonObject = new JSONObject(str);
		String searchId = jsonObject.getString("searchId");
		float total_fee_float = jsonObject.getFloat("total_fee");
		int total_fee_int = (int)(total_fee_float*100);
		String total_fee = String.valueOf(total_fee_int);
		//先检查orders里这个字段是否有数据，如果有，说明支付过，失败了。继续使用这个out_trade_no进行支付
		String out_trade_no = ordersMapper.selectBySearchId(searchId).getO_OutTradeNo();
		if (out_trade_no == null) {
			out_trade_no = UUID.randomUUID().toString().replaceAll("-", "");
		}
		//添加out_trade_no到数据库,设置正在支付为1，那么服务端进行操作时会检查这个isPayNow字段。如果为1，无法完结订单。
		//在success方法中，对这个isPayNow进行归0
		ordersMapper.updateOut_Trade_NoBySearchId(1, out_trade_no, searchId);
		
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("sub_openid", openId);
		paraMap.put("body", "郭利" + "-test");
		paraMap.put("out_trade_no", out_trade_no);
		paraMap.put("spbill_create_ip", ipAddress);
		paraMap.put("total_fee", "1"/*total_fee*/);
		paraMap.put("trade_type", "JSAPI");
		paraMap.put("sub_appid", this.service_sub_appid);
		paraMap.put("sub_mch_id", this.service_sub_mch_id);
		logger.info("paraMap: " + paraMap);
		
		final String SUCCESS_NOTIFY = "https://www.donghuastar.com/wxpay/success";
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(serviceWXPayConfig , SUCCESS_NOTIFY, false, useSandbox);
		
		Map<String, String> map = wxPay.unifiedOrder(wxPay.fillRequestData(paraMap), 15000, 15000);
		
		String prePayId = (String) map.get("prepay_id");
		logger.info("xmlStr为： " + map);
		
		Map<String, String> payMap = new HashMap<String, String>();
		payMap.put("appId", this.appId);
		
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
