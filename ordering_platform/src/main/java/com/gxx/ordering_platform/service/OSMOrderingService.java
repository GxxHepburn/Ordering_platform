package com.gxx.ordering_platform.service;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMOrderingService {
	
	@Autowired OrdersMapper ordersMapper;

	@Autowired WechatUserMapper wechatUserMapper;
	
	@Transactional
	public String userOrderList(int U_ID) {
		
		//获取该用户订单
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUIDOrderByIimeDESC(U_ID);
		
		return listToString(multi_Orders_Tab_Tabtypes);
	}

	@Transactional
	public String getOrderFormList(Map<String, Object> map) throws GeneralSecurityException {
		int pagenumInt = (int) map.get("pagenum");
		int pagesizeInt = (int) map.get("pagesize");
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		// 先进行空值判断过滤
		String O_UniqSearchID = map.get("O_UniqSearchID").toString();
		String U_OpenId = map.get("U_OpenId").toString();
		Integer U_ID = null;
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
		String PayStatusString = map.get("PayStatus").toString();
		Integer PayStatus = null;
		if (!"".equals(PayStatusString)) {
			PayStatus = Integer.valueOf(PayStatusString);
		}
		//TD
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		
		Date orderStartTimeDate = null;
		Date orderEndTimeDate = null;
		Date payStartTimeDate = null;
		Date payEndTimeDate = null;
		
		List<Date> datesList = new ArrayList<Date>();
		datesList.add(orderStartTimeDate);
		datesList.add(orderEndTimeDate);
		datesList.add(payStartTimeDate);
		datesList.add(payEndTimeDate);
		
		List<String> timeStringsList = new ArrayList<String>();
		timeStringsList.add(map.get("OrderStartTime").toString());
		timeStringsList.add(map.get("OrderEndTime").toString());
		timeStringsList.add(map.get("PayStartTime").toString());
		timeStringsList.add(map.get("PayEndTime").toString());
		
		for(int i = 0; i < timeStringsList.size(); i++) {
			if (!"".equals(timeStringsList.get(i))) {
				// 处理下单开始时间
				String newTimeString = timeStringsList.get(i).replace("Z", " UTC");
				
				try {
					datesList.set(i, format.parse(newTimeString));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = null;
		
		// 根据参数
		// 订单号不为空，直接根据订单号，查询订单
		if (!"".equals(O_UniqSearchID)) {
			multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUniqSearchIDOrderByIimeDESC(O_UniqSearchID);
		} else {
			if (!"".equals(U_OpenId)) {
				// 有商户号
				// 获得O_UID
				String real_U_OpenId = EncryptionAndDeciphering.deciphering(U_OpenId);
				WechatUser wechatUser = wechatUserMapper.getByUOpenId(real_U_OpenId);
				U_ID = wechatUser.getU_ID();
			} 
			multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUIDTabIDTabtypeIDOorderTimePayTimeOrderByIimeDESC(U_ID,
					TabId, TabTypeId, datesList.get(0), datesList.get(1), datesList.get(2), datesList.get(3), limitStart, pagesizeInt, PayStatus);
		}
		
		return listToString(multi_Orders_Tab_Tabtypes);
	}
	
	// 根据List<Multi_Orders_Tab_Tabtype> 返回值
	public String listToString(List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes) {
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONArray ordersJsonArray = new JSONArray();
		for (Multi_Orders_Tab_Tabtype multi_Orders_Tab_Tabtype : multi_Orders_Tab_Tabtypes) {
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("O_ID", multi_Orders_Tab_Tabtype.getO_ID());
			jsonObject.put("O_MID", multi_Orders_Tab_Tabtype.getO_MID());
			jsonObject.put("O_UID", multi_Orders_Tab_Tabtype.getO_UID());
			jsonObject.put("O_TID", multi_Orders_Tab_Tabtype.getO_TID());
			jsonObject.put("O_TotlePrice", multi_Orders_Tab_Tabtype.getO_TotlePrice());
			jsonObject.put("O_PayStatue", multi_Orders_Tab_Tabtype.getO_PayStatue());
			
			jsonObject.put("O_payMethod", multi_Orders_Tab_Tabtype.getO_PayMethod());
			
			//格式化时间
			jsonObject.put("O_OrderingTime", simpleDateFormat.format(multi_Orders_Tab_Tabtype.getO_OrderingTime()));
			if (multi_Orders_Tab_Tabtype.getO_PayTime() == null) {
				jsonObject.put("O_PayTime", "");
				jsonObject.put("O_OutTradeNo", "");
			} else {
				jsonObject.put("O_PayTime", simpleDateFormat.format(multi_Orders_Tab_Tabtype.getO_PayTime()));
				jsonObject.put("O_OutTradeNo", multi_Orders_Tab_Tabtype.getO_OutTradeNo());
			}
			
			jsonObject.put("O_Remarks", multi_Orders_Tab_Tabtype.getO_Remarks());
			jsonObject.put("O_TotleNum", multi_Orders_Tab_Tabtype.getO_TotleNum());
			jsonObject.put("O_UniqSearchID", multi_Orders_Tab_Tabtype.getO_UniqSearchID());
			
			jsonObject.put("O_isPayNow", multi_Orders_Tab_Tabtype.getO_isPayNow());
			jsonObject.put("O_ReturnNum", multi_Orders_Tab_Tabtype.getO_ReturnNum());
			
			String T_Name = multi_Orders_Tab_Tabtype.getT_Name();
			if (T_Name == null) {
				T_Name = "餐桌已删除";
			}
			jsonObject.put("T_Name", T_Name);
			String TT_Name = multi_Orders_Tab_Tabtype.getTT_Name();
			if (TT_Name == null) {
				TT_Name = "分类已删除";
			}
			jsonObject.put("TT_Name", TT_Name);
			
			ordersJsonArray.put(jsonObject);
		}
		
		newJsonObject.put("data", ordersJsonArray);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
