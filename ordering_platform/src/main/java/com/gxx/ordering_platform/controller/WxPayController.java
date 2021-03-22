package com.gxx.ordering_platform.controller;

import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Printer;
import com.gxx.ordering_platform.entity.Refund;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.entity.WxPayNotifyV0;
import com.gxx.ordering_platform.entity.WxRefundNotifyV0;
import com.gxx.ordering_platform.handler.OSMOrderingHandler;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrderReturnDetailMapper;
import com.gxx.ordering_platform.mapper.OrderReturnMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PayMapper;
import com.gxx.ordering_platform.mapper.PrinterMapper;
import com.gxx.ordering_platform.mapper.RefundMapper;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;
import com.gxx.ordering_platform.service.WechatOrderingService;
import com.gxx.ordering_platform.service.WxPayOtherService;
import com.gxx.ordering_platform.service.WxPayService;
import com.gxx.ordering_platform.wxPaySDK.WXPayUtil;
import com.gxx.ordering_platform.xpyunSDK.service.PrintService;
import com.gxx.ordering_platform.xpyunSDK.util.Config;
import com.gxx.ordering_platform.xpyunSDK.vo.ObjectRestResponse;
import com.gxx.ordering_platform.xpyunSDK.vo.PrintRequest;

@RestController
@RequestMapping("/wxpay")
public class WxPayController {
	
	@Autowired
	WxPayService wxPayService;
	
	@Autowired
	WechatOrderingService wechatOrderingService;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	RefundMapper refundMapper;
	
	@Autowired
	MerMapper merMapper;
	
	@Autowired
	PayMapper payMapper;
	
	@Autowired
	OrderReturnDetailMapper orderReturnDetailMapper;
	
	@Autowired
	OrderReturnMapper orderReturnMapper;
	
	@Autowired
	OrderDetailMapper orderDetailMapper;
	
	@Autowired
	OrderAddMapper orderAddMapper;
	
	@Autowired
	TabMapper tabMapper;
	
	@Autowired
	TabTypeMapper tabTypeMapper;
	
	@Autowired
	PrinterMapper printerMapper;
	
	@Autowired
	WxPayOtherService wxPayOtherService;
	
	@Value("${serviceNumber.mchKey}")
	private String mchKey;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	// 小程序正在使用的接口
	@Transactional
	@PostMapping("/pay/{openId}")
	@ResponseBody
	public String servicePay(HttpServletRequest request, @PathVariable String openId, @RequestBody String str) {
		
		//首先检查payStatus
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
			logger.error("ERROR", e);
		}
		return new JSONObject(payMap).toString();
	}
	
	@PostMapping("/{openId}")
	@ResponseBody
	@Transactional
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
			logger.error("ERROR", e);
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
		try {
			wxPayOtherService.success(request, param);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		
		Map<String, String> result = new HashMap<String, String>();
		if ("SUCCESS".equals(param.getReturn_code())) {
			result.put("return_code", "SUCCESS");
			result.put("return_msg", "OK");
		}
		String successReturn = null;
		try {
			successReturn =  WXPayUtil.mapToXml(result);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		return successReturn;
	}
	
	@RequestMapping(value = "/fail/{searchId}")
	@ResponseBody
	@Transactional
	public void fail(@PathVariable String searchId) {
		// 判断当前支付顺序，如果先支付后下单，则删除订单，以及相关信息，如果下单后支付，则更新isPay
		try {
			Orders orders = ordersMapper.getOrdersByUniqSearchID(searchId);
			Mer mer = merMapper.getMerByMID(orders.getO_MID());
			if (mer.getM_IsOrderWithPay() == 1) {
				this.deleteOrders(searchId);
			} else {
				wxPayService.updateIsPay(searchId, 0);
			}
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
	}
	
	// 删除订单方法
	@Transactional
	public void deleteOrders(String searchId) {
		Orders orders = ordersMapper.getOrdersByUniqSearchID(searchId);
		int oid = orders.getO_ID();
		// 删除退款表中R_OID = oid
		refundMapper.deleteByOID(oid);
		// 删除支付表中P_OID = oid
		payMapper.deleteByOID(oid);
		// 删除orderreturndetail 中ORD_OID = oid
		orderReturnDetailMapper.deleteByOID(oid);
		// 删除orderreturn 中OR_OID = oid
		orderReturnMapper.deleteByOID(oid);
		// 删除orderdetail 中OD_OID = oid
		orderDetailMapper.deleteByOID(oid);
		// 删除orderadd 中 OA_OID = oid
		orderAddMapper.deleteByOID(oid);
		// 删除orders中 O_ID = oid
		ordersMapper.deleteByOID(oid);
	}
	
	@RequestMapping(value = "/returnSuccess", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	@Transactional
	public String returnSuccess(HttpServletRequest request, @RequestBody WxRefundNotifyV0 param) {
		
		
		Map<String, String> result = new HashMap<String, String>();
		result.put("return_code", "SUCCESS");
		if ("SUCCESS".equals(param.getReturn_code())) {
			
			result.put("return_msg", "OK");
			// 解密
			String req_info = param.getReq_info();
			try {
				String keyMD5 = WXPayUtil.MD5(mchKey).toLowerCase();
				byte[] decodeReqInfo = Base64.getDecoder().decode(req_info);
				Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
				cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyMD5.getBytes(), "AES"));
				byte[] decoded = cipher.doFinal(decodeReqInfo);
				String decryptInfo = new String(decoded, "UTF-8");
				Map<String, String> reqInfoMap = WXPayUtil.xmlToMap(decryptInfo);
				
				// 根据refund_id查询退款记录，然后根据记录判断是否处理过，如果处理过，则什么也不做，否则，update退款记录
				String refund_id = reqInfoMap.get("refund_id");
				Refund refund = refundMapper.getByRefund_id(refund_id);
				if (refund.getR_Refund_Status() == null) {
					// 只有第一次收到通知时，才处理
					String settlement_total_fee = reqInfoMap.get("settlement_total_fee");
					String refund_request_source = reqInfoMap.get("refund_request_source");
					String refund_status = reqInfoMap.get("refund_status");
					String settlement_refund_fee = reqInfoMap.get("settlement_refund_fee");
					String success_time = reqInfoMap.get("success_time");
					String refund_recv_accout =  reqInfoMap.get("refund_recv_accout");
					String refund_account = reqInfoMap.get("refund_account");
					
					refundMapper.updateReturnSuccess(settlement_total_fee, refund_request_source, 
							refund_status, settlement_refund_fee, success_time, refund_recv_accout, 
							refund_account, refund.getR_ID());
					
					
					// 当接单时，并且页面停留在当前订单详情页面时，更新退款记录-websocket
					JSONObject wbssJsonObject = new JSONObject();
					wbssJsonObject.put("type", "4");
					wbssJsonObject.put("O_ID", refund.getR_OID());
					try {
						oSMOrderingHandler.sendTextMessage(refund.getR_MID(), wbssJsonObject.toString());
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						logger.error("ERROR", e1);
					}
					
				}
				
			} catch (Exception e) {
				logger.error("ERROR", e);
				// 手动回滚
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			}
		}
		String successReturn = null;
		try {
			successReturn =  WXPayUtil.mapToXml(result);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		return successReturn;
	}
}
