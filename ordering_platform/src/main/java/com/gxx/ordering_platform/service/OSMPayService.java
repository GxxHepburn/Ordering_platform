package com.gxx.ordering_platform.service;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.BankType;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_Pay_Orders_Tab_TabType;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Pay;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.BankTypeMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PayMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMPayService {
	
	@Autowired
	PayMapper payMapper;
	
	@Autowired
	BankTypeMapper bankTypeMapper;
	
	@Autowired
	MmaMapper mmaMapper;
	
	@Autowired
	WechatUserMapper wechatUserMapper;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	@Transactional
	public String getOrderPayForm(Map<String, Object> map) {
		
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		Pay pay = payMapper.getByO_ID(O_ID);
		
		if (pay == null) {
			//拼接json
			JSONObject newJsonObject = new JSONObject();
			
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 200);
			metaJsonObject.put("msg", "成功");
			
			JSONObject dataJsonObject = new JSONObject();
			dataJsonObject.put("orderPayForm", new JSONObject());
			
			newJsonObject.put("data", dataJsonObject);
			newJsonObject.put("meta", metaJsonObject);
			
			return newJsonObject.toString();
		}
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "成功");
		
		JSONObject dataJsonObject = new JSONObject();
		
		JSONObject payJsonObject = new JSONObject();
		// 格式化P_Time_End时间
		SimpleDateFormat simpleDateFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
		Date P_Time_End_Date = null;
		try {
			P_Time_End_Date = simpleDateFormatParse.parse(pay.getP_Time_End());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String P_Time_End_String = simpleDateFormat.format(P_Time_End_Date);
		payJsonObject.put("p_Time_End", P_Time_End_String);
		payJsonObject.put("p_Totle_Fee", pay.getP_Totle_Fee());
		payJsonObject.put("p_Fee_Type", pay.getP_Fee_Type());
		payJsonObject.put("p_Trade_Type", pay.getP_Trade_Type());
		payJsonObject.put("p_Transaction_Id", pay.getP_Transaction_Id());
		BankType bankType = bankTypeMapper.getByB_CharCode(pay.getP_Bank_Type());
		payJsonObject.put("p_Bank_Type", bankType.getB_Name());
		dataJsonObject.put("orderPayForm", payJsonObject);
		
		newJsonObject.put("meta", metaJsonObject);
		newJsonObject.put("data", dataJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String getPayFormList(Map<String, Object> map) throws GeneralSecurityException {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		// 先进行空值判断过滤
		String O_UniqSearchID = map.get("O_UniqSearchID").toString();
		String U_OpenId = map.get("U_OpenId").toString();
		String TransactionId = map.get("TransactionId").toString();
		String OutTradeNo = map.get("OutTradeNo").toString();
		if ("".equals(TransactionId)) {
			TransactionId = null;
		}
		if ("".equals(OutTradeNo)) {
			OutTradeNo = null;
		}
		Integer U_ID = null;
		Integer O_ID = null;
		
		String TabIdString = map.get("TabId").toString();
		Integer TabId = null;
		if (!"".equals(TabIdString)) {
			TabId = Integer.valueOf(TabIdString);
		}
		String TabTypeIdString = map.get("TabTypeId").toString();
		Integer TabTypeId = null;
		if (!"".equals(TabTypeIdString)) {
			TabTypeId = Integer.valueOf(TabTypeIdString);
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		SimpleDateFormat formatString = new SimpleDateFormat("yyyyMMddHHmmss");
		
		String payStartTimeString = null;
		String payEndTimeString = null;
		
		List<String> datesStringList = new ArrayList<String>();
		datesStringList.add(payStartTimeString);
		datesStringList.add(payEndTimeString);
		
		List<String> timeStringsList = new ArrayList<String>();
		
		String PayStartTime = "";
		if (map.get("PayStartTime") != null) {
			PayStartTime = map.get("PayStartTime").toString();
		}
		String PayEndTime = "";
		if (map.get("PayEndTime") != null) {
			PayEndTime = map.get("PayEndTime").toString();
		}
		
		timeStringsList.add(PayStartTime);
		timeStringsList.add(PayEndTime);
		
		for(int i = 0; i < timeStringsList.size(); i++) {
			if (!"".equals(timeStringsList.get(i))) {
				// 处理下单开始时间
				String newTimeString = timeStringsList.get(i).replace("Z", " UTC");
				
				try {
					datesStringList.set(i, formatString.format(new Date(format.parse(newTimeString).getTime())));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (!"".equals(U_OpenId)) {
			// 有商户号
			// 获得O_UID
			try {
				String real_U_OpenId = EncryptionAndDeciphering.deciphering(U_OpenId);
				WechatUser wechatUser = wechatUserMapper.getByUOpenId(real_U_OpenId);
				U_ID = wechatUser.getU_ID();
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("用户号解密错误!");
				U_ID = 0;
			}
		}
		
		if (!"".equals(O_UniqSearchID)) {
			// 有商户号
			// 获得O_ID
			try {
				Orders orders = ordersMapper.getOrdersByUniqSearchID(O_UniqSearchID);
				O_ID = orders.getO_ID();
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("订单号错误!");
				O_ID = 0;
			}
		} 
		
		List<Multi_Pay_Orders_Tab_TabType> multi_Pay_Orders_Tab_TabTypes = null;
		int total = 0;
		
		multi_Pay_Orders_Tab_TabTypes = payMapper.getByUID_UniqSearchID_OutTradeNo_TransactionId_PayTime_TabId_TabTypeId(
				m_ID, U_ID, O_ID, OutTradeNo, TransactionId, datesStringList.get(0), datesStringList.get(1), 
				TabTypeId, TabId, limitStart, pagesizeInt);
		total = payMapper.getPayTotalByUID_UniqSearchID_OutTradeNo_TransactionId_PayTime_TabId_TabTypeId(
				m_ID, U_ID, O_ID, OutTradeNo, TransactionId, datesStringList.get(0), datesStringList.get(1), 
				TabTypeId, TabId);
		
		// 格式化P_Time_End时间
		SimpleDateFormat simpleDateFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONArray payJsonArray = new JSONArray(multi_Pay_Orders_Tab_TabTypes);
		for(int i = 0; i < payJsonArray.length(); i++) {
			Multi_Pay_Orders_Tab_TabType multi_Pay_Orders_Tab_TabType =  multi_Pay_Orders_Tab_TabTypes.get(i);
			Date P_Time_End_Date = null;
			try {
				P_Time_End_Date = simpleDateFormatParse.parse(multi_Pay_Orders_Tab_TabType.getP_Time_End());
			} catch (Exception e) {
				logger.info("支付时间null");
			}
			String P_Time_End_String = null;
			try {
				P_Time_End_String = simpleDateFormat.format(P_Time_End_Date);
			} catch (Exception e) {
				logger.info("支付时间null");
			}
			
			payJsonArray.getJSONObject(i).put("p_Time_End", P_Time_End_String);
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("payFormList", payJsonArray);
		dataJsonObject.put("total", total);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
