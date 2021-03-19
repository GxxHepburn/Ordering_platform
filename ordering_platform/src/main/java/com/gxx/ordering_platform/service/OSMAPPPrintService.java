package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.OrderAdd;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Printer;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PrinterMapper;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;
import com.gxx.ordering_platform.xpyunSDK.service.PrintService;
import com.gxx.ordering_platform.xpyunSDK.util.Config;
import com.gxx.ordering_platform.xpyunSDK.vo.ObjectRestResponse;
import com.gxx.ordering_platform.xpyunSDK.vo.PrintRequest;

@Component
public class OSMAPPPrintService {
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired OrdersMapper ordersMapper;
	
	@Autowired MerMapper merMapper;
	
	@Autowired TabMapper tabMapper;
	
	@Autowired TabTypeMapper tabTypeMapper;
	
	@Autowired WechatOrderingService wechatOrderingService;
	
	@Autowired PrinterMapper printerMapper;
	
	@Autowired OrderDetailMapper orderDetailMapper;
	
	@Autowired OrderAddMapper orderAddMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	public String printTickt(Map<String, Object> map) {
		
		// 创建打印所需对象
		PrintService printService = new PrintService();
		PrintRequest printRequest = new PrintRequest();
		
		String printContent = "";
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		
		List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(O_ID);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Orders orders = ordersMapper.getordersByO_ID(O_ID);
		
		Mer mer = merMapper.getMerByMID(orders.getO_MID());
		Tab tab = tabMapper.getByTabId(orders.getO_TID());
		TabType tabType = tabTypeMapper.getByTabTypeId(tab.getT_TTID());
		// 拼接小票内容
		// 餐厅名
		printContent+=("<CB>"+mer.getM_Name()+"<BR>");
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><N>类型:  主动操作打印"+"<BR>");
		printContent+=("<L><N>单号:"+orders.getO_UniqSearchID()+"<BR>");
		printContent+=("<L><N>下单时间:"+format.format(orders.getO_OrderingTime())+"<BR>");
		String opt = "";
		if(orders.getO_PayTime() != null) {
			opt = format.format(orders.getO_PayTime());
		}
		printContent+=("<L><N>支付时间:"+opt+"<BR>");
		printContent+=("<L><N>餐桌区域:"+tabType.getTT_Name()+"<BR>");
		printContent+=("<L><N>餐桌:"+tab.getT_Name()+"<BR>");
		printContent+=("<L><N>用餐人数:"+orders.getO_NumberOfDiners()+"<BR>");
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><N>备注:"+orders.getO_Remarks()+"<BR>");
		printContent+=("<L><N>**************商品**************"+"<BR>");
		printContent+=("<BR>");
		
		for (int i = 0; i < orderDetails.size(); i++) {
			
			OrderDetail orderDetail = orderDetails.get(i);
			
			String OD_FName = orderDetail.getOD_FName();
			int realNum = orderDetail.getOD_RealNum();
			// 拼接小票菜品项
			printContent+=("<L><HB>"+OD_FName+"<BR>");
			printContent+=("<L><N> "+orderDetail.getOD_Spec()+" "+orderDetail.getOD_PropOne()+" "+orderDetail.getOD_PropTwo()+"<BR>");
			printContent+=("<R><N>x"+realNum+"   ￥"+orderDetail.getOD_RealPrice()+"<BR>");
		}
		
		// 拼接小票信息
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><B>合计:￥"+orders.getO_TotlePrice()+"<BR>");
		printContent+=("<L><N>********************************"+"<BR>");
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><N>打印时间:"+format.format(new Date())+"<BR>");
		
		// 获取打印机sn
		List<Printer> printers = printerMapper.getByMID(orders.getO_MID());
		// 设置打印内容
		printRequest.setContent(printContent);
		// 根据打印机数目，设置sn号，同时发送打印请求
		for (int i = 0; i < printers.size(); i++) {
			printRequest.setSn(printers.get(i).getP_No());
			printRequest.setVoice(1);
			Config.createRequestHeader(printRequest);
			ObjectRestResponse<String> result = printService.print(printRequest);
			logger.info(result.toString());
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "打印成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	public String notTakingPrintTickt (Map<String, Object> map) {
		
		
		// 创建打印所需对象
		PrintService printService = new PrintService();
		PrintRequest printRequest = new PrintRequest();
		
		String printContent = "";
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		
		int OA_ID = Integer.valueOf(map.get("OA_ID").toString());
		
		List<OrderDetail> orderDetails = orderDetailMapper.getByOA_ID(OA_ID);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Orders orders = ordersMapper.getordersByO_ID(O_ID);
		OrderAdd orderAdd = orderAddMapper.getOA_ID(OA_ID);
		
		Mer mer = merMapper.getMerByMID(orders.getO_MID());
		Tab tab = tabMapper.getByTabId(orders.getO_TID());
		TabType tabType = tabTypeMapper.getByTabTypeId(tab.getT_TTID());
		
		// 拼接小票内容
		// 餐厅名
		printContent+=("<CB>"+mer.getM_Name()+"<BR>");
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><N>类型:  主动操作打印"+"<BR>");
		printContent+=("<L><N>单号:"+orders.getO_UniqSearchID()+"<BR>");
		printContent+=("<L><N>下单时间:"+format.format(orderAdd.getOA_OrderingTime())+"<BR>");
		String opt = "";
		if(orders.getO_PayTime() != null) {
			opt = format.format(orders.getO_PayTime());
		}
		printContent+=("<L><N>支付时间:"+opt+"<BR>");
		printContent+=("<L><N>餐桌区域:"+tabType.getTT_Name()+"<BR>");
		printContent+=("<L><N>餐桌:"+tab.getT_Name()+"<BR>");
		printContent+=("<L><N>用餐人数:"+orders.getO_NumberOfDiners()+"<BR>");
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><N>备注:"+orders.getO_Remarks()+"<BR>");
		printContent+=("<L><N>**************商品**************"+"<BR>");
		printContent+=("<BR>");
		
		for (int i = 0; i < orderDetails.size(); i++) {
			
			OrderDetail orderDetail = orderDetails.get(i);
			
			String OD_FName = orderDetail.getOD_FName();
			int realNum = orderDetail.getOD_RealNum();
			// 拼接小票菜品项
			printContent+=("<L><HB>"+OD_FName+"<BR>");
			printContent+=("<L><N> "+orderDetail.getOD_Spec()+" "+orderDetail.getOD_PropOne()+" "+orderDetail.getOD_PropTwo()+"<BR>");
			printContent+=("<R><N>x"+realNum+"   ￥"+orderDetail.getOD_RealPrice()+"<BR>");
		}
		
		// 拼接小票信息
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><B>合计:￥"+orderAdd.getOA_TotlePrice()+"<BR>");
		printContent+=("<L><N>********************************"+"<BR>");
		printContent+=("<L><N>--------------------------------"+"<BR>");
		printContent+=("<L><N>打印时间:"+format.format(new Date())+"<BR>");
		
		// 获取打印机sn
		List<Printer> printers = printerMapper.getByMID(orders.getO_MID());
		// 设置打印内容
		printRequest.setContent(printContent);
		// 根据打印机数目，设置sn号，同时发送打印请求
		for (int i = 0; i < printers.size(); i++) {
			printRequest.setSn(printers.get(i).getP_No());
			printRequest.setVoice(1);
			Config.createRequestHeader(printRequest);
			ObjectRestResponse<String> result = printService.print(printRequest);
			logger.info(result.toString());
		}
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "打印成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
