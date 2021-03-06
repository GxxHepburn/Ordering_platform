package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gxx.ordering_platform.entity.COSN;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_Tabtype_Tab;
import com.gxx.ordering_platform.entity.TR;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.entity.UDS;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.QrCodeMapper;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;

@Component
public class OSMTabService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired TabMapper tabMapper;
	
	@Autowired TabTypeMapper tabTypeMapper;
	
	@Autowired OrdersMapper ordersMapper;
	
	@Autowired QrCodeMapper qrCodeMapper;

	@Transactional
	public String tabs(Map<String, Object> map) {
		
		String query = (String) map.get("query");
		int pagenumInt = (int) map.get("pagenum");
		int pagesizeInt = (int) map.get("pagesize");
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		// 链表查询-插入一个tt_id, tt_name
		List<Multi_Tabtype_Tab> multi_Tabtype_TabsList = tabMapper.getByMID(m_ID, limitStart, pagesizeInt, query);
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray tabsJsonArray = new JSONArray(multi_Tabtype_TabsList);
		dataJsonObject.put("tabs", tabsJsonArray);
		
		int tabsTotal = tabMapper.getTotalByMid(m_ID, query);
		dataJsonObject.put("total", tabsTotal);
		dataJsonObject.put("m_ID", m_ID);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String deleteTab(Map<String, Object> map) {
		
		int t_ID = Integer.valueOf(map.get("t_ID").toString());
		
		int m_ID = Integer.valueOf(map.get("TT_MID").toString());
		
		tabMapper.deleteByTID(t_ID);
		
		// 删除qrcode
		qrCodeMapper.delete(m_ID, t_ID);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "删除成功");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@Transactional
	public String editTab(Map<String, Object> map) {
		
		int t_ID = Integer.valueOf(map.get("t_ID").toString());
		int t_TTID = Integer.valueOf(map.get("t_TTID").toString());
		String t_Name = map.get("t_Name").toString();
		int t_PeopleOfDiners = Integer.valueOf(map.get("t_PeopleOfDiners").toString());
		
		tabMapper.updateByTID(t_ID, t_TTID, t_Name, t_PeopleOfDiners);
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@Transactional
	public String searchtabs(Map<String, Object> map) {
		
		if ("".equals(map.get("TT_ID"))) {
			return tabs(map);
		}
		
		String query = map.get("query").toString();
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int TT_ID = Integer.parseInt(map.get("TT_ID").toString());
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		// 链表查询-插入一个tt_id, tt_name
		List<Multi_Tabtype_Tab> multi_Tabtype_TabsList = tabMapper.getByMIDANDTTID(m_ID, limitStart, pagesizeInt, query, TT_ID);
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray tabsJsonArray = new JSONArray(multi_Tabtype_TabsList);
		dataJsonObject.put("tabs", tabsJsonArray);
		
		int tabsTotal = tabMapper.getTotalByMidANDTTID(m_ID, query, TT_ID);
		dataJsonObject.put("total", tabsTotal);
		dataJsonObject.put("m_ID", m_ID);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String addTab(Map<String, Object> map) {
		
		int T_TTID = Integer.valueOf(map.get("t_TTID").toString());
		String T_Name = map.get("t_Name").toString();
		int T_PeopleOfDiners = Integer.valueOf(map.get("t_PeopleOfDiners").toString());
		String mmngctUserName = (String) map.get("mmngctUserName");
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		Tab tab = new Tab();
		tab.setT_MID(m_ID);
		tab.setT_TTID(T_TTID);
		tab.setT_Name(T_Name);
		tab.setT_PeopleOfDiners(T_PeopleOfDiners);
		
		
		tabMapper.insert(tab);
		
		String url = "https://www.donghuastar.com/wechat/scan?res=" + m_ID + "&table=" + tab.getT_ID();
		// 插入qrcode
		qrCodeMapper.insert(m_ID, url, tab.getT_ID(), tab.getT_TTID());
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String ordersTabAndTabTypeOptions(Map<String, Object> map) {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		List<TabType> tabTypes = tabTypeMapper.getByMID(m_ID);
		
		JSONArray tabAndTabTypeJsonArray = new JSONArray();
		for (TabType tabType : tabTypes) {
			List<Tab> tabs = tabMapper.getByTTID(tabType.getTT_ID());
			JSONObject tabTypeJsonObject = new JSONObject();
			tabTypeJsonObject.put("value", tabType.getTT_ID());
			tabTypeJsonObject.put("label", tabType.getTT_Name());
			
			JSONArray tabJsonArray = new JSONArray();
			for (Tab tab : tabs) {
				JSONObject tabJsonObject = new JSONObject();
				tabJsonObject.put("value", tab.getT_ID());
				tabJsonObject.put("label", tab.getT_Name());
				
				tabJsonArray.put(tabJsonObject);
			}
			
			tabTypeJsonObject.put("children", tabJsonArray);
			
			tabAndTabTypeJsonArray.put(tabTypeJsonObject);
		}
		
		
		//拼接json
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("ordersTabAndTabTypeOptions", tabAndTabTypeJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String searchCOSNFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String COSNStartString = map.get("COSNStartString").toString();
		String COSNEndString = map.get("COSNEndString").toString();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date COSNStartDate = format.parse(COSNStartString);
		Date COSNEndDate = format.parse(COSNEndString);
		
		List<COSN> csons = tabMapper.searchCOSN(m_ID, COSNStartDate, COSNEndDate);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("COSNFormList", new JSONArray(csons));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String searchTRFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String TRDayString = map.get("TRDayString").toString();
		
		String TRStartString = TRDayString + " 00:00:00";
		String TREndString = TRDayString + " 23:59:59";
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date TRStartDate = format.parse(TRStartString);
		Date TREndDate = format.parse(TREndString);
		
		// 获取桌台数、餐位数
		// 交易数=开台数
		// 客人数
		
		TR tr = new TR();
		
		TR trTab = tabMapper.searchTR(m_ID);
		
		TR trOrders = ordersMapper.searchTR(m_ID, TRStartDate, TREndDate);
		
		tr.setTabnum(trTab.getTabnum());
		tr.setTabPersonNum(trTab.getTabPersonNum());
		
		tr.setTradeNum(trOrders.getTradeNum());
		tr.setOpeningNum(trOrders.getOpeningNum());
		tr.setNumberOfDiners(trOrders.getNumberOfDiners());
		tr.setAttendance(((float) tr.getNumberOfDiners())/((float) tr.getTabPersonNum()));
		tr.setOpeningRate(((float) tr.getOpeningNum())/((float) tr.getTabnum()));
		if (tr.getOpeningNum() <= tr.getTabnum()) {
			tr.setTurnoverRate(0);
		} else {
			tr.setTurnoverRate(((float) (tr.getOpeningNum() - tr.getTabnum()))/((float) tr.getTabnum()));
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		List<TR> trs = new ArrayList<TR>();
		trs.add(tr);
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("TRFormList", new JSONArray(trs));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchTRWFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String TRWStartString = map.get("TRWStartString").toString();
		String TRWEndString = map.get("TRWEndString").toString();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date TRWStartDate = format.parse(TRWStartString);
		Date TRWEndDate = format.parse(TRWEndString);
		
		// 获取桌台数、餐位数
		// 交易数=开台数
		// 客人数
		
		TR tr = new TR();
		
		TR trTab = tabMapper.searchTR(m_ID);
		
		TR trOrders = ordersMapper.searchTR(m_ID, TRWStartDate, TRWEndDate);
		
		tr.setTabnum(trTab.getTabnum());
		tr.setTabPersonNum(trTab.getTabPersonNum());
		
		tr.setTradeNum(trOrders.getTradeNum());
		tr.setOpeningNum(trOrders.getOpeningNum());
		tr.setNumberOfDiners(trOrders.getNumberOfDiners());
		tr.setAttendance(((float) tr.getNumberOfDiners())/((float) tr.getTabPersonNum()));
		tr.setOpeningRate(((float) tr.getOpeningNum())/((float) tr.getTabnum()));
		if (tr.getOpeningNum() <= tr.getTabnum()) {
			tr.setTurnoverRate(0);
		} else {
			tr.setTurnoverRate(((float) (tr.getOpeningNum() - tr.getTabnum()))/((float) tr.getTabnum()));
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		List<TR> trs = new ArrayList<TR>();
		trs.add(tr);
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("TRWFormList", new JSONArray(trs));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchTRMFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String TRMStartString = map.get("TRMStartString").toString();
		String TRMEndString = map.get("TRMEndString").toString();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date TRMStartDate = format.parse(TRMStartString);
		Date TRMEndDate = format.parse(TRMEndString);
		
		// 获取桌台数、餐位数
		// 交易数=开台数
		// 客人数
		
		TR tr = new TR();
		
		TR trTab = tabMapper.searchTR(m_ID);
		
		// 共用
		TR trOrders = ordersMapper.searchTR(m_ID, TRMStartDate, TRMEndDate);
		
		tr.setTabnum(trTab.getTabnum());
		tr.setTabPersonNum(trTab.getTabPersonNum());
		
		tr.setTradeNum(trOrders.getTradeNum());
		tr.setOpeningNum(trOrders.getOpeningNum());
		tr.setNumberOfDiners(trOrders.getNumberOfDiners());
		tr.setAttendance(((float) tr.getNumberOfDiners())/((float) tr.getTabPersonNum()));
		tr.setOpeningRate(((float) tr.getOpeningNum())/((float) tr.getTabnum()));
		if (tr.getOpeningNum() <= tr.getTabnum()) {
			tr.setTurnoverRate(0);
		} else {
			tr.setTurnoverRate(((float) (tr.getOpeningNum() - tr.getTabnum()))/((float) tr.getTabnum()));
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		List<TR> trs = new ArrayList<TR>();
		trs.add(tr);
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("TRMFormList", new JSONArray(trs));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
