package com.gxx.ordering_platform.service;

import java.security.GeneralSecurityException;
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

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_OrderAdd_Tab_Tabtype_Orders;
import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.Pay;
import com.gxx.ordering_platform.entity.Refund;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMAPPOrderingService {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired OrderAddMapper orderAddMapper;
	
	@Autowired OrdersMapper ordersMapper;
	
	@Autowired WechatUserMapper wechatUserMapper;	

	@Autowired OSMOrderingService oSMOrderingService;

	@Transactional
	public String notTakingOrerAddFormList (Map<String, Object> map) throws Exception {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		Integer tabId = null;
		if (!"".equals(map.get("totalTabId").toString())) {
			tabId = Integer.valueOf(map.get("totalTabId").toString());
		}
		
		Date startDate = null;
		Date endDate = null;
		
		String startString = map.get("OrderStartTime").toString();
		String endString = map.get("OrderEndTime").toString();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if (!"".equals(startString)) {
			startDate = format.parse(startString);
		}
		
		if (!"".equals(endString)) {
			endDate = format.parse(endString);
		}
		
		String O_UniqSearchID = map.get("O_UniqSearchID").toString();
		
		List<Multi_OrderAdd_Tab_Tabtype_Orders> multi_OrderAdd_Tab_Tabtypes_Orderses = null;
		
		// 订单号不为空，直接根据订单号，查询订单
		if (!"".equals(O_UniqSearchID)) {
			multi_OrderAdd_Tab_Tabtypes_Orderses = orderAddMapper.getNotTakingByUniqSearchID(O_UniqSearchID);
		} else {
			multi_OrderAdd_Tab_Tabtypes_Orderses = orderAddMapper.getNotTakingByMIDTabIdOrderingTimeOrderByOrderingTime(m_ID, tabId, startDate, endDate, limitStart, pagesizeInt);
		}
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取未接单列表成功!");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray notTakingOrerAddJsonArray = new JSONArray();
		
		for (int i = 0; i < multi_OrderAdd_Tab_Tabtypes_Orderses.size(); i++) {
			JSONObject notTakingOrerAddJSONObject = new JSONObject(multi_OrderAdd_Tab_Tabtypes_Orderses.get(i));
			notTakingOrerAddJSONObject.put("OA_OrderingTime", simpleDateFormat.format(multi_OrderAdd_Tab_Tabtypes_Orderses.get(i).getOA_OrderingTime()));
			if (multi_OrderAdd_Tab_Tabtypes_Orderses.get(i).getO_PayTime() == null) {
				notTakingOrerAddJSONObject.put("o_PayTime", "");
			} else {
				notTakingOrerAddJSONObject.put("o_PayTime", simpleDateFormat.format(multi_OrderAdd_Tab_Tabtypes_Orderses.get(i).getO_PayTime()));
			}
			
			notTakingOrerAddJsonArray.put(notTakingOrerAddJSONObject);
		}
		
		
		dataJsonObject.put("notTakingOrerAddFormList", notTakingOrerAddJsonArray);
		
		newJsonObject.put("meta", metaJsonObject);
		newJsonObject.put("data", dataJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String getOrderFormList(Map<String, Object> map) throws GeneralSecurityException {
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = null;
		
		int total = 0;
		
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
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
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
		
		String OrderStartTime = "";
		if (map.get("OrderStartTime") != null) {
			OrderStartTime = map.get("OrderStartTime").toString();
		}
		String OrderEndTime = "";
		if (map.get("OrderEndTime") != null) {
			OrderEndTime = map.get("OrderEndTime").toString();
		}
		String PayStartTime = "";
		if (map.get("PayStartTime") != null) {
			PayStartTime = map.get("PayStartTime").toString();
		}
		String PayEndTime = "";
		if (map.get("PayEndTime") != null) {
			PayEndTime = map.get("PayEndTime").toString();
		}
		timeStringsList.add(OrderStartTime);
		timeStringsList.add(OrderEndTime);
		timeStringsList.add(PayStartTime);
		timeStringsList.add(PayEndTime);
		
		for(int i = 0; i < timeStringsList.size(); i++) {
			if (!"".equals(timeStringsList.get(i))) {
				// 处理下单开始时间
//				String newTimeString = timeStringsList.get(i).replace("Z", " UTC");
				
				try {
					datesList.set(i, format.parse(timeStringsList.get(i)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		// 根据参数
		// 订单号不为空，直接根据订单号，查询订单
		if (!"".equals(O_UniqSearchID)) {
			multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUniqSearchIDOrderByIimeDESC(O_UniqSearchID);
			if (multi_Orders_Tab_Tabtypes.size() == 0) {
				total = 0;
			} else {
				total = 1;
			}
		} else {
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
			multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUIDTabIDTabtypeIDOorderTimePayTimeOrderByIimeDESC(U_ID,
					TabId, TabTypeId, datesList.get(0), datesList.get(1), datesList.get(2), datesList.get(3), m_ID, limitStart, pagesizeInt, PayStatus);
			total = ordersMapper.getOrdersTotalByUIDTabIDTabtypeIDOorderTimePayTime(U_ID,
					TabId, TabTypeId, datesList.get(0), datesList.get(1), datesList.get(2), datesList.get(3), m_ID, PayStatus);
		}
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("orderFormList", oSMOrderingService.listToString(multi_Orders_Tab_Tabtypes));
		dataJsonObject.put("total", total);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
