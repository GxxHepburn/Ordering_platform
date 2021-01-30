package com.gxx.ordering_platform.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.WxPayNotifyV0;
import com.gxx.ordering_platform.handler.OSMOrderingHandler;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.service.WechatOrderingService;
import com.gxx.ordering_platform.service.WxPayService;
import com.gxx.ordering_platform.wxPaySDK.WXPayUtil;

@RestController
@RequestMapping("/wxpay")
public class WxPayController {
	
	@Autowired
	WxPayService wxPayService;
	
	@Autowired
	WechatOrderingService wechatOrderingService;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	// 小程序正在使用的接口
	@Transactional
	@PostMapping("/pay/{openId}")
	@ResponseBody
	public String servicePay(HttpServletRequest request, @PathVariable String openId, @RequestBody String str) {
		
		//首先检查payStatus
//		logger.info(str);
		JSONObject jsonObject = new JSONObject(str);
		String searchId = jsonObject.getString("searchId");
		boolean payStatus = wechatOrderingService.getPayStatus(searchId);
		if (payStatus) {
			return "1";
		}
		
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
			payMap = wxPayService.wxServicePay(openId, ip, str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new JSONObject(payMap).toString();
	}
	
	@PostMapping("/{openId}")
	@ResponseBody
	public String pay(HttpServletRequest request, @PathVariable String openId){
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
	
	@Resource
	OSMOrderingHandler oSMOrderingHandler;
	
	// 微信会发很多次success通知
	@RequestMapping(value = "/success", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	@Transactional
	public String success(HttpServletRequest request, @RequestBody WxPayNotifyV0 param) {
//		logger.info("success: " + param.toString());
//		logger.info("return_code: " + param.getReturn_code());
		// 先判断是否处理过，如果处理过，就跳过
		Orders orders = ordersMapper.getOrderByO_OutTradeNo(param.getOut_trade_no());
		System.out.println("收到");
		if (orders.getO_PayStatue() == 0) {
			System.out.println("内部收到");
		
			//修改isPayNow，同时设置payStatus,payTime
			Date payTime = new Date();
			wxPayService.updatePaied(param.getOut_trade_no(), 0, 1, payTime);
			
			wxPayService.insertPay(param);
			
			// ，用websocket连接，发送语音播报，前台自动打印客人小票，如果是在接单页面，则刷新接单页面订单数据
			Multi_Orders_Tab_Tabtype multi_Orders_Tab_Tabtype = ordersMapper.getOrderWithTNameAndTTNameByO_OutTradeNo(param.getOut_trade_no());
			// 生成语音内容Json
			JSONObject wbssJsonObject = new JSONObject();
			wbssJsonObject.put("type", "1");
			String voiceString = multi_Orders_Tab_Tabtype.getTT_Name() + "," + multi_Orders_Tab_Tabtype.getT_Name() + "的客人支付" + (Float.valueOf(param.getTotal_fee())/100.00f) + "元";
			wbssJsonObject.put("voiceText", voiceString);
			wbssJsonObject.put("O_ID", multi_Orders_Tab_Tabtype.getO_ID());
			try {
				oSMOrderingHandler.sendTextMessage(multi_Orders_Tab_Tabtype.getO_MID(), wbssJsonObject.toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		Map<String, String> result = new HashMap<String, String>();
		if ("SUCCESS".equals(param.getReturn_code())) {
			result.put("return_code", "SUCCESS");
			result.put("return_msg", "OK");
		}
//		logger.info(String.valueOf(param));
		String successReturn = null;
		try {
			successReturn =  WXPayUtil.mapToXml(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return successReturn;
	}
	
	@RequestMapping(value = "/fail/{searchId}")
	@ResponseBody
	public void fail(@PathVariable String searchId) {
		wxPayService.updateIsPay(searchId, 0);
	}
}
