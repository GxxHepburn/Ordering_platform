package com.gxx.ordering_platform.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.FoodProperty;
import com.gxx.ordering_platform.entity.FoodSpecifications;
import com.gxx.ordering_platform.entity.Image;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.FoodPropertyMapper;
import com.gxx.ordering_platform.mapper.FoodSpecificationsMapper;
import com.gxx.ordering_platform.mapper.FoodTypeMapper;
import com.gxx.ordering_platform.mapper.ImageMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.utils.PropertiesUtils;

@Component
public class OSMFoodService {
	
	@Autowired FoodMapper foodMapper;
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired FoodPropertyMapper foodPropertyMapper;
	
	@Autowired FoodSpecificationsMapper foodSpecificationsMapper;
	
	@Autowired FoodTypeMapper foodTypeMapper;
	
	@Autowired ImageMapper imageMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Transactional
	public String uploadFoodImg(MultipartFile file) throws Exception {
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		JSONObject metaJsonObject = new JSONObject();
		String imgUrl;
		
		// 判断是否存在，如果存在，则不上传并获取存在图片地址
		String isImgRepeatString = isImgRepeat(file);
		if (!"-1".equals(isImgRepeatString)) {
			imgUrl = PropertiesUtils.get("netSet", "OSMDomain") + "/static/FoodImges/" + isImgRepeatString;
			
			// 返回唯一地址
	        metaJsonObject.put("status", 200);
			metaJsonObject.put("msg", "上传成功");
			
			JSONObject dataJsonObject = new JSONObject();
			dataJsonObject.put("imgUrl", imgUrl);
			
			newJsonObject.put("meta", metaJsonObject);
			newJsonObject.put("data", dataJsonObject);
			
			return newJsonObject.toString();
		}
		// 如果不存在，则走下面分支,并将图片信息存入image表
		
		// 生成唯一地址,以及存储的绝对地址
		String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1); // 后缀
        String uuID = UUID.randomUUID().toString();
        String imgPath = PropertiesUtils.get("netSet", "OSMAbsolutePath") + "/FoodImges/"
				+ uuID + "." + suffix;
        imgUrl = PropertiesUtils.get("netSet", "OSMDomain") + "/static/FoodImges/" + uuID + "." + suffix;
        
		
        
        // 将图片名,hash值存入数据库
        byte[] fileUploadBytes = file.getBytes();
        MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(fileUploadBytes);
		byte[] mdResult = md.digest();
		String mdString = new BigInteger(1, mdResult).toString(16);
		String I_Name = uuID + "." + suffix;
		imageMapper.insert(mdString, I_Name);
		
		
		
		// 取出图片，放到唯一地址上
        File localFile = new File(imgPath);
		
        try {
			file.transferTo(localFile);
		} catch (Exception e) {
			logger.error("ERROR", e);
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
	public String editFood(Map<String, Object> map) {
		
		// 获取数据
		//获取food数据
		int f_id = Integer.parseInt(map.get("F_ID").toString());
		int f_ftid = Integer.parseInt(map.get("F_FTID").toString());
		int f_mid = Integer.parseInt(map.get("F_MID").toString());
		String f_name = map.get("F_Name").toString();
		String f_imageUrl = "";
		float f_price = Float.parseFloat(map.get("F_Price").toString());
		String f_unit = map.get("F_Unit").toString();
		int f_stock = Integer.parseInt( map.get("F_Stock").toString());
		String f_tag = "";
		try {
			f_imageUrl = map.get("F_ImageUrl").toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			f_tag = map.get("F_Tag").toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		// 先进行数据库操作！
		// 先对food表操作
		// 如果f_ImageUrl 是空的，就不修改该项
		if (f_imageUrl == "") {
			// 不修改该项
			foodMapper.updateFoodInfoWithoutImgUrlByFID(f_ftid, f_name, f_price, f_unit, f_stock, f_tag, f_id);
		} else {
			//修改该项
			foodMapper.updateFoodInfoByFID(f_ftid, f_name, f_imageUrl, f_price, f_unit, f_stock, f_tag, f_id);
		}
		
		// 再删除fs，fp中相应的项
		foodSpecificationsMapper.deleteByFS_FID(f_id);
		foodPropertyMapper.deleteByFP_FID(f_id);
		
		// 在再fs，fp中添加相应的项
		JSONObject getJsonObject = new JSONObject(map);
		// 获取fs数据
		JSONArray fsJsonArray = getJsonObject.getJSONArray("F_Specs");
		for (int i = 0; i < fsJsonArray.length(); i++) {
			JSONObject fsItemJsonObject = fsJsonArray.getJSONObject(i);
			String fs_key = fsItemJsonObject.getString("FS_Key");
			float fs_value = fsItemJsonObject.getFloat("FS_Value");
			foodSpecificationsMapper.insert(f_id, f_ftid, f_mid, fs_key, fs_value);
		}
	
		// 获取fp数据
		JSONArray fpJsonArray = (JSONArray) getJsonObject.getJSONArray("F_Properties");
		for (int i = 0; i < fpJsonArray.length(); i++) {
			JSONObject fpItemJsonObject = fpJsonArray.getJSONObject(i);
			String fpItemName = fpItemJsonObject.getString("FP_Name");
			String fpItemValueOne = "";
			String fpItemValueTwo = "";
			String fpItemValueThree = "";
			String fpItemValueFour = "";
			String fpItemValueFive = "";
			try {
				fpItemValueOne = fpItemJsonObject.getString("FP_ValueOne");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueTwo = fpItemJsonObject.getString("FP_ValueTwo");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueThree = fpItemJsonObject.getString("FP_ValueThree");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueFour = fpItemJsonObject.getString("FP_ValueFour");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueFive = fpItemJsonObject.getString("FP_ValueFive");
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			String[] fpItemArray = new String[5];
			fpItemArray[0] = fpItemValueOne;
			fpItemArray[1] = fpItemValueTwo;
			fpItemArray[2] = fpItemValueThree;
			fpItemArray[3] = fpItemValueFour;
			fpItemArray[4] = fpItemValueFive;
			
			List<String> ftItemList = new ArrayList<String>();
//			int ftItemNullNumber = 0; 这种方式会导致小程序 属性显示空白框框
			for (int j = 0; j < fpItemArray.length; j++) {
				if (fpItemArray[j] != "") {
					ftItemList.add(fpItemArray[j]);
					continue;
				}
//				ftItemNullNumber ++;
			}
//			for (int j = 0; j < ftItemNullNumber; j++) {
//				ftItemList.add("");
//			}
			if (ftItemList.size() == 1) {
				
			} else if (ftItemList.size() == 2) {
				foodPropertyMapper.insertTwo(f_id, f_ftid, f_mid, fpItemName, ftItemList.get(0), ftItemList.get(1));
			} else if (ftItemList.size() == 3) {
				foodPropertyMapper.insertThree(f_id, f_ftid, f_mid, fpItemName, ftItemList.get(0), ftItemList.get(1),
						ftItemList.get(2));
			} else if (ftItemList.size() == 4) {
				foodPropertyMapper.insertFour(f_id, f_ftid, f_mid, fpItemName, ftItemList.get(0), ftItemList.get(1),
						ftItemList.get(2), ftItemList.get(3));
			} else {
				foodPropertyMapper.insert(f_id, f_ftid, f_mid, fpItemName, ftItemList.get(0), ftItemList.get(1),
						ftItemList.get(2), ftItemList.get(3), ftItemList.get(4));
			}
		}
		
		// 在文件夹中删除对应的图片---第一版不对照片进行删除，每天特定时间，系统管理员手动清除无效图片
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		newJsonObject.put("meta", metaJsonObject);

		return newJsonObject.toString();
	}
	
	@Transactional
	public String deleteFood(Map<String, Object> map) {
		
		// 获取数据
		//获取food数据
		int f_id = Integer.parseInt(map.get("F_ID").toString());
		
		// 删除食物
		foodMapper.deleteFoodByFID(f_id);
		
		// 再删除fs，fp中相应的项
		foodSpecificationsMapper.deleteByFS_FID(f_id);
		foodPropertyMapper.deleteByFP_FID(f_id);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String addFood (Map<String, Object> map) {
		
		// 获取数据
		//获取food数据
		int f_ftid = Integer.parseInt(map.get("F_FTID").toString());
		String f_name = map.get("F_Name").toString();
		String f_imageUrl = "";
		float f_price = Float.parseFloat(map.get("F_Price").toString());
		String f_unit = map.get("F_Unit").toString();
		int f_stock = Integer.parseInt( map.get("F_Stock").toString());
		String f_tag = "";
		try {
			f_imageUrl = map.get("F_ImageUrl").toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			f_tag = map.get("F_Tag").toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		// 获取mid
		String mmngctUserName = (String) map.get("mmngctUserName");
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		Food food = new Food();
		food.setF_FTID(f_ftid);
		food.setF_Name(f_name);
		food.setF_MID(m_ID);
		food.setF_ImageUrl(f_imageUrl);
		food.setF_Price(f_price);
		food.setF_Unit(f_unit);
		food.setF_Stock(f_stock);
		food.setF_Tag(f_tag);
		food.setF_Statue(1);
		
		// 先对food表操作
		foodMapper.insert(food);
		
		// 在再fs，fp中添加相应的项
		JSONObject getJsonObject = new JSONObject(map);
		// 获取fs数据
		JSONArray fsJsonArray = getJsonObject.getJSONArray("F_Specs");
		for (int i = 0; i < fsJsonArray.length(); i++) {
			JSONObject fsItemJsonObject = fsJsonArray.getJSONObject(i);
			String fs_key = fsItemJsonObject.getString("FS_Key");
			float fs_value = fsItemJsonObject.getFloat("FS_Value");
			foodSpecificationsMapper.insert(food.getF_ID(), f_ftid, m_ID, fs_key, fs_value);
		}
		
		// 获取fp数据
		JSONArray fpJsonArray = (JSONArray) getJsonObject.getJSONArray("F_Properties");
		for (int i = 0; i < fpJsonArray.length(); i++) {
			JSONObject fpItemJsonObject = fpJsonArray.getJSONObject(i);
			String fpItemName = fpItemJsonObject.getString("FP_Name");
			String fpItemValueOne = "";
			String fpItemValueTwo = "";
			String fpItemValueThree = "";
			String fpItemValueFour = "";
			String fpItemValueFive = "";
			try {
				fpItemValueOne = fpItemJsonObject.getString("FP_ValueOne");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueTwo = fpItemJsonObject.getString("FP_ValueTwo");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueThree = fpItemJsonObject.getString("FP_ValueThree");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueFour = fpItemJsonObject.getString("FP_ValueFour");
			} catch (Exception e) {
				// TODO: handle exception
			}
			try {
				fpItemValueFive = fpItemJsonObject.getString("FP_ValueFive");
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			String[] fpItemArray = new String[5];
			fpItemArray[0] = fpItemValueOne;
			fpItemArray[1] = fpItemValueTwo;
			fpItemArray[2] = fpItemValueThree;
			fpItemArray[3] = fpItemValueFour;
			fpItemArray[4] = fpItemValueFive;
			
			List<String> ftItemList = new ArrayList<String>();
//					int ftItemNullNumber = 0; 这种方式会导致小程序 属性显示空白框框
			for (int j = 0; j < fpItemArray.length; j++) {
				if (fpItemArray[j] != "") {
					ftItemList.add(fpItemArray[j]);
					continue;
				}
//						ftItemNullNumber ++;
			}
//					for (int j = 0; j < ftItemNullNumber; j++) {
//						ftItemList.add("");
//					}
			if (ftItemList.size() == 1) {
				// 只有一个属性的时候，就没有必要存在这个属性了啊
			} else if (ftItemList.size() == 2) {
				foodPropertyMapper.insertTwo(food.getF_ID(), f_ftid, m_ID, fpItemName, ftItemList.get(0), ftItemList.get(1));
			} else if (ftItemList.size() == 3) {
				foodPropertyMapper.insertThree(food.getF_ID(), f_ftid, m_ID, fpItemName, ftItemList.get(0), ftItemList.get(1),
						ftItemList.get(2));
			} else if (ftItemList.size() == 4) {
				foodPropertyMapper.insertFour(food.getF_ID(), f_ftid, m_ID, fpItemName, ftItemList.get(0), ftItemList.get(1),
						ftItemList.get(2), ftItemList.get(3));
			} else {
				foodPropertyMapper.insert(food.getF_ID(), f_ftid, m_ID, fpItemName, ftItemList.get(0), ftItemList.get(1),
						ftItemList.get(2), ftItemList.get(3), ftItemList.get(4));
			}
		}

		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchGoods(Map<String, Object> map) {
		// 待完善的query 检索功能-包含检索名称、分类、tag三项
		String query = (String) map.get("query");
		int pagenumInt = (int) map.get("pagenum");
		int pagesizeInt = (int) map.get("pagesize");
		Integer FT_ID = null;
		try {
			FT_ID = Integer.parseInt(map.get("FT_ID").toString());
		} catch (Exception e) {
			// TODO: handle exception
		}
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		List<Food> foods = foodMapper.getBtMID(m_ID, FT_ID, limitStart, pagesizeInt, query);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONArray foodsJsonArray = new JSONArray();
		
		for (Food food : foods) {
			JSONObject foodJsonObject = new JSONObject();
			foodJsonObject.put("F_ID", food.getF_ID());
			foodJsonObject.put("F_FTID", food.getF_FTID());
			// 获取该商品对应的分类名称
			
			String foodTypeName = foodTypeMapper.getByFTID(food.getF_FTID()).getFT_Name();
			foodJsonObject.put("F_FTName", foodTypeName);
			
			foodJsonObject.put("F_MID", food.getF_MID());
			foodJsonObject.put("F_Name", food.getF_Name());
			foodJsonObject.put("F_ImageUrl", food.getF_ImageUrl());
			foodJsonObject.put("F_Price", food.getF_Price());
			foodJsonObject.put("F_Statue", food.getF_Statue());
			foodJsonObject.put("F_Unit", food.getF_Unit());
			foodJsonObject.put("F_Stock", food.getF_Stock());  
			foodJsonObject.put("F_SalesVolume", food.getF_SalesVolume());
			foodJsonObject.put("F_Tag", food.getF_Tag());
			foodJsonObject.put("F_Sort", food.getF_Sort());
			
			// 获取该food对应的property
			List<FoodProperty> foodProperties = foodPropertyMapper.getByMIDAndFTIDAndFID(food.getF_MID(), food.getF_FTID(), food.getF_ID());
			JSONArray proesJsonArray = new JSONArray(foodProperties);
			foodJsonObject.put("F_Properties", proesJsonArray);
			
			// 获取该food对应的spec
			List<FoodSpecifications> foodSpecifications = foodSpecificationsMapper.getByMIDAndFTIDAndFID(food.getF_MID(), food.getF_FTID(), food.getF_ID());
			JSONArray specsJsonArray = new JSONArray(foodSpecifications);
			foodJsonObject.put("F_Specs", specsJsonArray);
			
			foodsJsonArray.put(foodJsonObject);
		}
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("goods", foodsJsonArray);
		
		int foodsTotal = foodMapper.getTotalByMID(m_ID, FT_ID, query);
		dataJsonObject.put("total", foodsTotal);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String onePunchUpGoods(Map<String, Object> map) {
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		foodMapper.updateStockByM_ID_AND_F_StockNotZero(m_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "一键上货成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String changeFoodStatue(Map<String, Object> map) {
		int F_ID = Integer.valueOf(map.get("F_ID").toString());
		int F_Statue = Integer.valueOf(map.get("F_Statue").toString());
		
		foodMapper.updateFoodStatueByF_ID(F_ID, F_Statue);
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "修改商品状态成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String onePunchDisableOrAble(Map<String, Object> map) {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		int statue = Integer.valueOf(map.get("statue").toString());
		
		foodMapper.updateFoodStatueByMID(m_ID, statue);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	// 判断图片是否存在
	public String isImgRepeat (MultipartFile file) throws Exception {
		// 获取图片字节流
		byte[] fileUploadBytes = file.getBytes();
		
		// 计算图片hash值
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(fileUploadBytes);
		byte[] mdResult = md.digest();
		String mdString = new BigInteger(1, mdResult).toString(16);
		// 数据库检索相同hash值图片List
		List<Image> images = imageMapper.getByI_Hash_Uniq(mdString);
		if (images.size() == 0) {
			return "-1";
		}
		// 和List中每一张图片比对
		for (int i = 0; i < images.size(); i++) {
			String fileName = images.get(i).getI_Name();
	        String imgPath = PropertiesUtils.get("netSet", "OSMAbsolutePath") + "/FoodImges/"
					+ fileName;
	        File fileHaved = new File(imgPath);
	        // 如果字节长度相同，再逐字节对比
	        if (fileUploadBytes.length == fileHaved.length()) {
	        	try (InputStream inputStream = new FileInputStream(fileHaved);) {
			        byte[] fileHavedBytes = new byte[(int)fileHaved.length()];
			        inputStream.read(fileHavedBytes);
			        int j = 0;
			        for (; j < fileUploadBytes.length; j++) {
			        	if (fileUploadBytes[j] != fileHavedBytes[j]) {
			        		break;
			        	}
			        }
			        if (j == fileUploadBytes.length) {
			        	// 相同
			        	return images.get(i).getI_Name();
			        }
	        	}
		        
	        }
		}
		return "-1";
	}
}
