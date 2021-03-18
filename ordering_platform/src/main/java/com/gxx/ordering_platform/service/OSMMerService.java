package com.gxx.ordering_platform.service;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.utils.PropertiesUtils;

@Component
public class OSMMerService {
	
	@Autowired MerMapper merMapper;
	
	@Autowired MmaMapper mmaMapper;

	@Transactional
	public String getMerInfo(Map<String, Object> map) {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		Mer mer = merMapper.getMerByMID(m_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		
		JSONObject merInfoJSObject = new JSONObject(mer);
		// 消除敏感信息商家特约商户号
		merInfoJSObject.put("m_Sub_Mch_ID", "");
		dataJsonObject.put("merInfo", merInfoJSObject);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	public String uploadMerImg(MultipartFile file) {
		
		// 生成唯一地址,以及存储的绝对地址
		String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1); // 后缀
        String uuID = UUID.randomUUID().toString();
        String imgPath = PropertiesUtils.get("netSet", "OSMAbsolutePath") + "/img-wxMer/"
				+ uuID;
        imgPath = imgPath + "." + suffix;
        String imgUrl = PropertiesUtils.get("netSet", "OSMDomain") + "/static/img-wxMer/" + uuID + "." + suffix;
        
		// 取出图片，放到唯一地址上
        File localFile = new File(imgPath);
        
        //拼接json
		JSONObject newJsonObject = new JSONObject();
		JSONObject metaJsonObject = new JSONObject();
		
	      try {
			file.transferTo(localFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			metaJsonObject.put("status", "500");
			metaJsonObject.put("msg", "上传失败");
			
			newJsonObject.put("meta", metaJsonObject);
			return newJsonObject.toString();
		} 
	      
		// 返回唯一地址
	      metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "上传成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("imgUrl", imgUrl);
		
		newJsonObject.put("meta", metaJsonObject);
		newJsonObject.put("data", dataJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String changeMerInfo(Map<String, Object> map) {
		// update，然后getMerInfo
		int m_ID = Integer.valueOf(map.get("m_ID").toString());
		String m_Name = map.get("m_Name").toString();
		String m_Address = map.get("m_Address").toString();
		String m_Phone = map.get("m_Phone").toString();
		String m_BeginTime = map.get("m_BeginTime").toString();
		String m_EndTime = map.get("m_EndTime").toString();
		String m_img = map.get("m_img").toString();
		
		merMapper.updateMerInfo(m_ID, m_Name, m_Address, m_Phone, m_BeginTime, m_EndTime, m_img);
		return getMerInfo(map);
	}

	@Transactional
	public void openMer(int M_ID) {
		merMapper.updateM_IsInOpenTimeByM_ID(M_ID, 1);
	}
	
	@Transactional
	public void closeMer(int M_ID) {
		merMapper.updateM_IsInOpenTimeByM_ID(M_ID, 0);
	}
}
