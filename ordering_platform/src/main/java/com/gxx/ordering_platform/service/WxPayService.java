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

import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Pay;
import com.gxx.ordering_platform.entity.Refund;
import com.gxx.ordering_platform.entity.WxPayNotifyV0;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PayMapper;
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
	private String body;
	
	@Value("${wechat.appId}")
	private String appId;
	
	@Value("${merchantNumber.mchKey}")
	private String mchKey;
	
	//---------------------------------------------------
	
	@Value("${serviceNumber.appid}")
	private String service_appid;
	
	@Value("${serviceNumber.mch_id}")
	private String service_mch_id;
	
	@Value("${serviceNumber.sub_appid}")
	private String service_sub_appid;
	
	@Value("${serviceNumber.sub_mch_id}")
	private String service_sub_mch_id;
	
	@Value("${serviceNumber.mchKey}")
	private String ServiceMchKey;
	
	
	@Autowired
	ServiceWXPayConfig serviceWXPayConfig;
	
	
	@Autowired
	MerchantWXPayConfig merchantWXPayConfig;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	PayMapper payMapper;
	
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
//		logger.info("paraMap: " + paraMap);
		
		final String SUCCESS_NOTIFY = "https://www.donghuastar.com/wxpay/success";
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(merchantWXPayConfig, SUCCESS_NOTIFY, false, useSandbox);
		
		Map<String, String> map = wxPay.unifiedOrder(wxPay.fillRequestData(paraMap), 15000, 15000);
		
		String prePayId = (String) map.get("prepay_id");
//		logger.info("xmlStr为: " + map);
		
		
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
		
//		logger.info("payMap: " + payMap);
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
		paraMap.put("body", "商铺名称-消费");
		paraMap.put("out_trade_no", out_trade_no);
		paraMap.put("spbill_create_ip", ipAddress);
		paraMap.put("total_fee", "1"/*total_fee*/);
		paraMap.put("trade_type", "JSAPI");
		paraMap.put("sub_appid", this.service_sub_appid);
		paraMap.put("sub_mch_id", this.service_sub_mch_id);
//		logger.info("paraMap: " + paraMap);
		
		final String SUCCESS_NOTIFY = "https://www.donghuastar.com/wxpay/success";
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(serviceWXPayConfig , SUCCESS_NOTIFY, false, useSandbox);
		
		Map<String, String> map = wxPay.unifiedOrder(wxPay.fillRequestData(paraMap), 15000, 15000);
		
		String prePayId = (String) map.get("prepay_id");
		
		Map<String, String> payMap = new HashMap<String, String>();
		payMap.put("appId", this.appId);
		
		payMap.put("timeStamp", WXPayUtil.getCurrentTimestamp() + "");
		payMap.put("nonceStr", WXPayUtil.generateNonceStr());
		payMap.put("signType", WXPayConstants.HMACSHA256);
		payMap.put("package", "prepay_id=" + prePayId);
		
		String paySign = null;
		paySign = WXPayUtil.generateSignature(payMap, this.ServiceMchKey, WXPayConstants.SignType.HMACSHA256);
		payMap.put("paySign", paySign);
		
		return payMap;
	}
	
	// 商家客户端退款
	public Map<String, String> returnMoneyFromWechat(String out_trade_no, String out_refund_no, String totle_fee, String refund_fee) throws Exception {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("sub_appid", this.service_sub_appid);
		paraMap.put("sub_mch_id", this.service_sub_mch_id);
		paraMap.put("out_trade_no", out_trade_no);
		paraMap.put("out_refund_no", out_refund_no);
//		paraMap.put("total_fee", totle_fee);
//		paraMap.put("refund_fee", refund_fee);
		paraMap.put("total_fee", "1");
		paraMap.put("refund_fee", "1");
		
		final String SUCCESS_NOTIFY = "https://www.donghuastar.com/wxpay/retturnSuccess";
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(serviceWXPayConfig , SUCCESS_NOTIFY, false, useSandbox);
		
		Map<String, String> resultap = wxPay.refund(paraMap, 15000, 15000);
		
		return resultap;
	}
	// 退款查询
	public Map<String, String> refundQuery (Refund refund) throws Exception {
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("refund_id", refund.getR_Refund_Id());
		paraMap.put("sub_mch_id", this.service_sub_mch_id);
		boolean useSandbox = false;
		WXPay wxPay = new WXPay(serviceWXPayConfig, false, useSandbox);
		Map<String, String> resultap = wxPay.refundQuery(paraMap, 15000, 15000);
		return resultap;
	}
	
	@Transactional
	public void insertPay(WxPayNotifyV0 param) {
		String O_OutTrade_No= param.getOut_trade_no();
		// 插入之前先检查
		Pay pay = payMapper.getByO_OutTrade_No(O_OutTrade_No);
		if (pay != null) {
			return;
		}
				
		Orders order = ordersMapper.getOrderByO_OutTradeNo(O_OutTrade_No);
		
		pay = new Pay();
		pay.setP_MID(order.getO_MID());
		pay.setP_OID(order.getO_ID());
		pay.setP_UID(order.getO_UID());
		
		pay.setP_Appid(param.getAppid());
		pay.setP_Attach(param.getAttach());
		pay.setP_Bank_Type(param.getBank_type());
		pay.setP_Fee_Type(param.getFee_type());
		pay.setP_Is_Subscribe(param.getIs_subscribe());
		pay.setP_Mch_Id(param.getMch_id());
		pay.setP_Nonce_Str(param.getNonce_str());
		pay.setP_Openid(param.getOpenid());
		pay.setP_Out_Trade_No(param.getOut_trade_no());
		pay.setP_Result_Code(param.getResult_code());
		pay.setP_Return_Code(param.getReturn_code());
		pay.setP_Sign(param.getSign());
		pay.setP_Time_End(param.getTime_end());
		pay.setP_Totle_Fee(param.getTotal_fee());
		pay.setP_Coupon_Fee(param.getCoupon_fee());
		pay.setP_Coupon_Count(param.getCoupon_count());
		pay.setP_Coupon_Type(param.getCoupon_type());
		pay.setP_Coupon_Id(param.getCoupon_id());
		pay.setP_Trade_Type(param.getTrade_type());
		pay.setP_Transaction_Id(param.getTransaction_id());
		
		payMapper.insert(pay);
	}
}
