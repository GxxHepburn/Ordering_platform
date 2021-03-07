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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_WechatUser_Orders;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.UDS;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMUsersService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired WechatUserMapper wechatUserMapper;
	
	@Autowired OrdersMapper ordersMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	@Transactional
	public String users(Map<String, Object> map) throws JSONException, GeneralSecurityException {
		
		String query = map.get("query").toString();
		String pagenum = map.get("pagenum").toString();
		String pagesize = map.get("pagesize").toString();
		String mmngctUserName = map.get("mmngctUserName").toString();
		String O_UniqSearchId = map.get("O_UniqSearchId").toString();
		int touchButton = Integer.valueOf(map.get("touchButton").toString());
		
		
		int pagenumInt = Integer.valueOf(pagenum);
		int pagesizeInt = Integer.valueOf(pagesize);
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int mID = mmngct.getMMA_ID();
		
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		List<Multi_WechatUser_Orders> multi_WechatUser_Orders = null;
		int totalNeedWechatUsers = 0;
		
		if (touchButton == 1) {
			// 解密
			if (!"".equals(query)) {
				try {
					query = EncryptionAndDeciphering.deciphering(query);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("解密时错误");
					query = "";
				}
			} else {
				query = null;
			}
			
			
			multi_WechatUser_Orders = wechatUserMapper.getByUOpenIdLike(mID, query, limitStart, pagesizeInt);
			
			totalNeedWechatUsers = wechatUserMapper.getTotalByOpenIdLike(mID, query);
		} else if (touchButton == 2) {
			Orders orders = ordersMapper.getOrdersByUniqSearchID(O_UniqSearchId);
			Integer U_ID = null;
			try {
				U_ID = orders.getO_UID();
			} catch (Exception e) {
				logger.info("订单号错误!");
				if (!"".equals(O_UniqSearchId)) {
					U_ID = 0;
				}
			}
			
			multi_WechatUser_Orders = wechatUserMapper.getByUID(mID, U_ID, limitStart, pagesizeInt);
			
			totalNeedWechatUsers = wechatUserMapper.getTotalByUID(mID, U_ID);
		}
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		
		dataJsonObject.put("total", totalNeedWechatUsers);
		dataJsonObject.put("pagenum", pagenumInt);
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		JSONArray userJsonArray = new JSONArray();
		for (Multi_WechatUser_Orders multi_WechatUser_Order : multi_WechatUser_Orders) {
			
			JSONObject jsonObject = new JSONObject();
			
			jsonObject.put("U_ID", multi_WechatUser_Order.getU_ID());
			jsonObject.put("U_OpenId", EncryptionAndDeciphering.encryption(multi_WechatUser_Order.getU_OpenId()));
			
			//格式化时间
			jsonObject.put("U_LoginTime", simpleDateFormat.format(multi_WechatUser_Order.getU_LoginTime()));
			jsonObject.put("U_RegisterTime", simpleDateFormat.format(multi_WechatUser_Order.getU_RegisterTime()));
			jsonObject.put("O_OrderingTime", simpleDateFormat.format(multi_WechatUser_Order.getO_OrderingTime()));
			jsonObject.put("U_Status", multi_WechatUser_Order.getU_Status());
			
			userJsonArray.put(jsonObject);
		}
		
		dataJsonObject.put("users", userJsonArray);
		
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String searchUDSFormList (Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String UDSStartString = map.get("UDSStartString").toString();
		String UDSEndString = map.get("UDSEndString").toString();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date UDSStartDate = format.parse(UDSStartString);
		Date UDSEndDate = format.parse(UDSEndString);
		
		UDS uds = new UDS();
		
		// 获取这个时间段用户
		UDS udsUserNum = wechatUserMapper.searchUDSUserNum(m_ID, UDSStartDate, UDSEndDate);
		
		UDS udsNewUserNum = wechatUserMapper.searchUDSNewUserNum(m_ID, UDSStartDate, UDSEndDate);
		
		UDS udsConsume = wechatUserMapper.searchUDSConsume(m_ID, UDSStartDate, UDSEndDate);
		
		
		
		uds.setUserNum(udsUserNum.getUserNum());
		uds.setNewUserNum(udsNewUserNum.getNewUserNum());
		uds.setConsumeNum(udsConsume.getConsumeNum());
		uds.setConsumeCount(udsConsume.getConsumeCount());
		uds.setTotalPrice(udsConsume.getTotalPrice());
		if (uds.getUserNum() > 0) {
			uds.setAverageConsumption(uds.getTotalPrice()/((float) uds.getUserNum())); 
		} else {
			uds.setAverageConsumption(0);
		}
		
		List<UDS> udses = new ArrayList<UDS>();
		udses.add(uds);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("UDSFormList", new JSONArray(udses));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
