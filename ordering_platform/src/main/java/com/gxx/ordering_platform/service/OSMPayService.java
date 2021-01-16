package com.gxx.ordering_platform.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.BankType;
import com.gxx.ordering_platform.entity.Pay;
import com.gxx.ordering_platform.mapper.BankTypeMapper;
import com.gxx.ordering_platform.mapper.PayMapper;

@Component
public class OSMPayService {
	
	@Autowired
	PayMapper payMapper;
	
	@Autowired
	BankTypeMapper bankTypeMapper;

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
}
