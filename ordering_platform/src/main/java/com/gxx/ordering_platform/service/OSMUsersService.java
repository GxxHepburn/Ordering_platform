package com.gxx.ordering_platform.service;

import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_WechatUser_Orders;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMUsersService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired WechatUserMapper wechatUserMapper;

	@Transactional
	public String users(String query, String pagenum, String pagesize, String mmngctUserName) throws JSONException, GeneralSecurityException {
		
		// 解密
		if (!"".equals(query)) {
			try {
				query = EncryptionAndDeciphering.deciphering(query);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				query = "";
			}
		} else {
			query = null;
		}
		
		int pagenumInt = Integer.valueOf(pagenum);
		int pagesizeInt = Integer.valueOf(pagesize);
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int mID = mmngct.getMMA_ID();
		
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		List<Multi_WechatUser_Orders> multi_WechatUser_Orders = wechatUserMapper.getByUOpenIdLike(mID, query, limitStart, pagesizeInt);
		
		int totalNeedWechatUsers = wechatUserMapper.getTotalByOpenIdLike(mID, query);
		
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
}
