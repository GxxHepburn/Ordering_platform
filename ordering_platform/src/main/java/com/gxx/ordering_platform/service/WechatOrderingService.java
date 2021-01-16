package com.gxx.ordering_platform.service;

import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.gxx.ordering_platform.entity.Food;
import com.gxx.ordering_platform.entity.OrderAdd;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.handler.OSMOrderingHandler;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;

@Component
public class WechatOrderingService {
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	WechatUserMapper wechatUserMapper;
	
	@Autowired
	FoodMapper foodMapper;
	
	@Autowired
	OrderDetailMapper orderDetailMapper;
	
	@Autowired
	TabMapper tabMapper;
	
	@Autowired
	TabTypeMapper tabTypeMapper;
	
	@Autowired
	OrderAddMapper orderAddMapper;
	
	@Autowired
	WebApplicationContext webApplicationContext;

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	OSMOrderingHandler oSMOrderingHandler;
	
	@Transactional
	public String ordering(String str) {
		//获取参数
		JSONObject jsonObject = new JSONObject(str);
		String openid = jsonObject.getString("openid");
		int totalNum = jsonObject.getInt("totalNum");
		int numberOfDiners = jsonObject.getInt("numberOfDiners");
		float totalPrice = jsonObject.getFloat("totalPrice");
		int mid = jsonObject.getInt("mid");
		logger.info("wechatOrderingService_mid: " + mid);
		int tid = jsonObject.getInt("tid");
		String remark = jsonObject.getString("remark");
		JSONArray ordersJsonArray = jsonObject.getJSONArray("orders");
		Date orderingTime = new Date();
		WechatUser wechatUser = wechatUserMapper.getByUOpenId(openid);
		String O_UniqSearchID = getOrderSearchID(mid, tid, orderingTime);
		logger.info("orders: " + ordersJsonArray);
		logger.info("O_UniqSearchID: " + O_UniqSearchID);
		
		Orders orders = new Orders();
		orders.setO_MID(mid);
		orders.setO_UID(wechatUser.getU_ID());
		orders.setO_TID(tid);
		orders.setO_PayStatue(0);
		orders.setO_OrderingTime(orderingTime);
		orders.setO_Remarks(remark);
		orders.setO_TotleNum(totalNum);
		orders.setO_UniqSearchID(O_UniqSearchID);
		orders.setO_NumberOfDiners(numberOfDiners);
		
		ordersMapper.insert(orders);
		
		OrderAdd orderAdd = new OrderAdd();
		orderAdd.setOA_OID(orders.getO_ID());
		orderAdd.setOA_Sort(1);
		orderAdd.setOA_MID(mid);
		orderAdd.setOA_UID(wechatUser.getU_ID());
		orderAdd.setOA_TID(tid);
		orderAdd.setOA_OrderingTime(orderingTime);
		orderAdd.setOA_TotleNum(totalNum);
		// 设置接单为0
		orderAdd.setOA_IsTaking("0");
		orderAddMapper.insert(orderAdd);
		
		float totalReturnPrice = 0.00f;
		for(int i=0; i<ordersJsonArray.length(); i++) {
			JSONObject orderDetailJsonObject = ordersJsonArray.getJSONObject(i);
			OrderDetail orderDetail = getByOrdersJsonArray(orders, orderAdd, orderDetailJsonObject);
			//获取现在真是的库存
			logger.info("OD_FID: " + orderDetail.getOD_FID());
			int nowStock = foodMapper.getStockByFID(orderDetail.getOD_FID());
			//传入点菜数量，当触发退款时，再核减退款菜品数量
			int nowSalesNum = foodMapper.getSalesNumByFID(orderDetail.getOD_FID());
			int realNum = 0;
			int overSellNum = 0;
			int D_value = 0;
			if (nowStock < 0) {
				realNum = orderDetail.getOD_Num();
				//不需要更新库存
			} else {
				D_value = nowStock - orderDetail.getOD_Num();
				if (D_value < 0) {
					JSONObject newJsonObject = new JSONObject();
					
					JSONObject metaJsonObject = new JSONObject();
					metaJsonObject.put("status", 410);
					metaJsonObject.put("msg", "下单失败");
					
					JSONObject dataJsonObject = new JSONObject();
					dataJsonObject.put("waringMsg", orderDetail.getOD_FName() + " 目前仅剩: " + nowStock + " 份, " + " 请调整数量后再下单!");
					
					newJsonObject.put("meta", metaJsonObject);
					newJsonObject.put("data", dataJsonObject);
					
					return newJsonObject.toString();
					// 直接返回错误信息json,meta=410
					
				} else {
					realNum = orderDetail.getOD_Num();
				}
				//设置菜品库存D_value
				foodMapper.updateStockByFID(D_value, orderDetail.getOD_FID());
				
			}
			
			totalReturnPrice += overSellNum * orderDetail.getOD_RealPrice();
			//更新数据库中销量
			//设置菜品销量nowSalesNum+orderDetail.getOD_Num()
			foodMapper.updateSalesVolumeByFID(nowSalesNum+orderDetail.getOD_Num(), orderDetail.getOD_FID());
			
			String OD_FName = foodMapper.getByFoodId(orderDetail.getOD_FID()).getF_Name();
			orderDetail.setOD_FName(OD_FName);
			orderDetail.setOD_RealNum(realNum);
			orderDetailMapper.insert(orderDetail);
			logger.info("OD_ID: " + orderDetail.getOD_ID());
		}
		orders.setO_TotlePrice(totalPrice - totalReturnPrice);
		ordersMapper.updateTotlePrice(orders.getO_ID(), orders.getO_TotlePrice());
		

		orderAdd.setOA_TotlePrice(totalPrice - totalReturnPrice);
		orderAddMapper.updateTotlePrice(orderAdd.getOA_ID(), orderAdd.getOA_TotlePrice());
		
		// websocket通知前端
		JSONObject wbssJsonObject = new JSONObject();
		wbssJsonObject.put("type", "2");
		String voiceString = "您有新的订单了,请尽快接单！";
		wbssJsonObject.put("voiceText", voiceString);
		wbssJsonObject.put("O_ID", orderAdd.getOA_OID());
		try {
			oSMOrderingHandler.sendTextMessage(orderAdd.getOA_MID(), wbssJsonObject.toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return O_UniqSearchID;
	}
	
	//生成订单号
	private String getOrderSearchID(int mid, int tid, Date orderingTime) {
		return "M" + mid + "T" + tid + "Y" 
				+ (orderingTime.getYear()+1900) + "M" 
				+ (orderingTime.getMonth()+1) + "D" + orderingTime.getDate() 
				+ "H" + orderingTime.getHours() + "M" + orderingTime.getMinutes() 
				+ "S" + orderingTime.getSeconds();
	}
	
	//根据JSONObject转化为OrderDetail对象
	private OrderDetail getByOrdersJsonArray(Orders orders, OrderAdd orderAdd, JSONObject orderDetailJsonObject) {
		OrderDetail orderDetail = new OrderDetail();
		String OD_FName = orderDetailJsonObject.getString("name");
		int OD_OID = orders.getO_ID();
		int OD_OAID = orderAdd.getOA_ID();
		int OD_FID = orderDetailJsonObject.getInt("id");
		int OD_FoodState = 0;
		float OD_RealPrice = orderDetailJsonObject.getFloat("price");
		String OD_Spec = orderDetailJsonObject.getString("specs");
		String OD_PropOne = "";
		String OD_PropTwo = "";
		JSONArray propJsonArray = orderDetailJsonObject.getJSONArray("property");
		for(int i=0; i<propJsonArray.length(); i++) {
			if (i==0) {
				OD_PropOne = propJsonArray.getString(i);
			}
			if (i==1) {
				OD_PropTwo = propJsonArray.getString(i);
			}
		}
		int OD_Num = orderDetailJsonObject.getInt("num");
		
		orderDetail.setOD_OID(OD_OID);
		orderDetail.setOD_FID(OD_FID);
		orderDetail.setOD_FoodState(OD_FoodState);
		orderDetail.setOD_RealPrice(OD_RealPrice);
		orderDetail.setOD_Spec(OD_Spec);
		orderDetail.setOD_PropOne(OD_PropOne);
		orderDetail.setOD_PropTwo(OD_PropTwo);
		orderDetail.setOD_Num(OD_Num);
		orderDetail.setOD_FName(OD_FName);
		orderDetail.setOD_OAID(OD_OAID);
		
		
		return orderDetail;
	}
	
	public String add(String str) {
		//根据orderId,然后insertfood
		JSONObject jsonObject = new JSONObject(str);
		JSONArray ordersJsonArray = jsonObject.getJSONArray("orders");
		String orderSearchId = jsonObject.getString("orderSearchId");
		int nowTotalNum = jsonObject.getInt("totalNum");
		float nowTotalPrice = jsonObject.getFloat("totalPrice");
		//获取Orders
		Orders orders = ordersMapper.selectBySearchId(orderSearchId);
		int totalNum = orders.getO_TotleNum() + nowTotalNum;
		float totalPrice = orders.getO_TotlePrice() + nowTotalPrice;
		
		//加上总数和totalPrice
		float totalReturnPrice = 0.00f;
		
		OrderAdd orderAdd = new OrderAdd();
		orderAdd.setOA_OID(orders.getO_ID());
		// 查一下有orderAdd中有多少个O_ID=orders.getO_ID()的，然后加一就是Sort了
		int count = orderAddMapper.selectCountByOA_OID(orders.getO_ID());
		orderAdd.setOA_Sort(1 + count);
		orderAdd.setOA_MID(orders.getO_MID());
		orderAdd.setOA_UID(orders.getO_UID());
		orderAdd.setOA_TID(orders.getO_TID());
		orderAdd.setOA_OrderingTime(new Date());
		orderAdd.setOA_TotleNum(totalNum);
		orderAdd.setOA_IsTaking("0");
		orderAddMapper.insert(orderAdd);
		
		for(int i=0; i<ordersJsonArray.length(); i++) {
			JSONObject orderDetailJsonObject = ordersJsonArray.getJSONObject(i);
			OrderDetail orderDetail = getByOrdersJsonArray(orders, orderAdd, orderDetailJsonObject);
			logger.info("OD_FID: " + orderDetail.getOD_FID());
			int nowStock = foodMapper.getStockByFID(orderDetail.getOD_FID());
			int nowSalesNum = foodMapper.getSalesNumByFID(orderDetail.getOD_FID());
			int realNum = 0;
			int overSellNum = 0;
			int D_value = 0;
			if (nowStock < 0) {
				realNum = orderDetail.getOD_Num();
			} else {
				D_value = nowStock - orderDetail.getOD_Num();
				if (D_value < 0) {
					JSONObject newJsonObject = new JSONObject();
					
					JSONObject metaJsonObject = new JSONObject();
					metaJsonObject.put("status", 410);
					metaJsonObject.put("msg", "下单失败");
					
					JSONObject dataJsonObject = new JSONObject();
					dataJsonObject.put("waringMsg", orderDetail.getOD_FName() + " 目前仅剩: " + nowStock + " 份, " + " 请调整数量后再下单!");
					
					newJsonObject.put("meta", metaJsonObject);
					newJsonObject.put("data", dataJsonObject);
					
					return newJsonObject.toString();
				} else {
					realNum = orderDetail.getOD_Num();
				}
				//设置菜品库存D_value
				foodMapper.updateStockByFID(D_value, orderDetail.getOD_FID());
				
			}
			totalReturnPrice += overSellNum * orderDetail.getOD_RealPrice();
			//更新数据库中销量
			//设置菜品销量nowSalesNum+orderDetail.getOD_Num()
			foodMapper.updateSalesVolumeByFID(nowSalesNum+orderDetail.getOD_Num(), orderDetail.getOD_FID());
			
			String OD_FName = foodMapper.getByFoodId(orderDetail.getOD_FID()).getF_Name();
			orderDetail.setOD_FName(OD_FName);
			orderDetail.setOD_RealNum(realNum);
			orderDetailMapper.insert(orderDetail);
			logger.info("OD_ID: " + orderDetail.getOD_ID());
		}
		ordersMapper.updateNumAndPriceBySearchId(orderSearchId, totalNum, totalPrice - totalReturnPrice);
		
		orderAdd.setOA_TotlePrice(nowTotalPrice - totalReturnPrice);
		orderAddMapper.updateTotlePrice(orderAdd.getOA_ID(), orderAdd.getOA_TotlePrice());
		
		// websocket通知前端
		JSONObject wbssJsonObject = new JSONObject();
		wbssJsonObject.put("type", "2");
		String voiceString = "您有新的订单了,请尽快接单！";
		wbssJsonObject.put("voiceText", voiceString);
		wbssJsonObject.put("O_ID", orderAdd.getOA_OID());
		try {
			oSMOrderingHandler.sendTextMessage(orderAdd.getOA_MID(), wbssJsonObject.toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return "-1";
	}
	
	public boolean getPayStatus(String searchId) {
		Orders orders = ordersMapper.selectBySearchId(searchId);
		if (orders.getO_PayStatue() != 0) {
			return true;
		} else {
			return false;
		}
	}
	
	//获得home订单列表
	@Transactional
	public String home(String str) {
		JSONObject jsonObject = new JSONObject(str);
		String openid = jsonObject.getString("openid");
		//未完成付款订单-payStatus==0||3,按照时间排序
		int o_uid = wechatUserMapper.getByUOpenId(openid).getU_ID();
		logger.info("o_uid: " + o_uid);
		logger.info("openid: " + openid);
		List<Orders> nowOrders = ordersMapper.getOrdersOrderByTimeNow(o_uid);
		List<Orders> finishedOrders = ordersMapper.getOrdersOrderByTimeFinished(o_uid);
		List<Orders> returnOrders = ordersMapper.getOrdersOrderByTimeReturn(o_uid);
		
		JSONArray nowJsonArray = new JSONArray(nowOrders);
		JSONArray finishedJsonArray = new JSONArray(finishedOrders);
		JSONArray returnJsonArray = new JSONArray(returnOrders);
		
		JSONObject ansJsonObject = new JSONObject();
		ansJsonObject.put("nowOrders", nowJsonArray);
		ansJsonObject.put("finishedOrders", finishedJsonArray);
		ansJsonObject.put("returnOrders", returnJsonArray);
		
		return ansJsonObject.toString();
	}
	
	@Transactional
	public String touchDetail(String str) {
		JSONObject jsonObject = new JSONObject(str);
		int t_id = jsonObject.getInt("tableId"); 
		int o_id = jsonObject.getInt("orderID");
		int m_id = jsonObject.getInt("res");
		
		Tab tab = tabMapper.getByTabId(t_id);
		String tabTypeName = "已删除";
		String tableName = "已删除";
		
		if (tab != null) {
			int tt_id = tab.getT_TTID();
			TabType tabType = tabTypeMapper.getByTabTypeId(tt_id);
			
			tabTypeName = tabType.getTT_Name();
			tableName = tab.getT_Name();
		}
		
		List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(o_id);
		JSONArray jsonArray = new JSONArray();
		int returnTotalPrice = 0;
		int hasReturn = 0;
		for(int i=0; i<orderDetails.size(); i++) {
			JSONObject orderDetailJsonObject = new JSONObject();
			orderDetailJsonObject.put("id", orderDetails.get(i).getOD_FID());
			Food food = foodMapper.getByFoodId(orderDetails.get(i).getOD_FID());
			String foodName = "";
			if (food == null) {
				foodName = "已删除";
			} else {
				foodName = food.getF_Name();
			}
			orderDetailJsonObject.put("name", foodName);
			orderDetailJsonObject.put("price", orderDetails.get(i).getOD_RealPrice());
			orderDetailJsonObject.put("specs", orderDetails.get(i).getOD_Spec());
			JSONArray proJsonArray = new JSONArray();
			proJsonArray.put(orderDetails.get(i).getOD_PropOne());
			proJsonArray.put(orderDetails.get(i).getOD_PropTwo());
			orderDetailJsonObject.put("property", proJsonArray);
			orderDetailJsonObject.put("num", orderDetails.get(i).getOD_Num());
			orderDetailJsonObject.put("realNum", orderDetails.get(i).getOD_RealNum());
			
			returnTotalPrice += (orderDetails.get(i).getOD_Num()- orderDetails.get(i).getOD_RealNum()) * orderDetails.get(i).getOD_RealPrice();
			
			jsonArray.put(orderDetailJsonObject);
		}
		logger.info(orderDetails.toString());
		
		if (returnTotalPrice > 0) {
			hasReturn = 1;
		}
		
		WeChatInitMenuService weChatInitMenuService = (WeChatInitMenuService)webApplicationContext.getBean("weChatInitMenuService");
		
		JSONObject returnJsonObject = new JSONObject();
		returnJsonObject.put("tabName", tableName);
		returnJsonObject.put("tabTypeName", tabTypeName);
		returnJsonObject.put("alreadyOrders", jsonArray);
		returnJsonObject.put("menu", weChatInitMenuService.initMenu(String.valueOf(m_id)));
		
		returnJsonObject.put("returnTotalPrice", returnTotalPrice);
		returnJsonObject.put("hasReturn", hasReturn);
		
		logger.info("menu: " + weChatInitMenuService.initMenu(String.valueOf(m_id)).toString());
		return returnJsonObject.toString();
	}
}
