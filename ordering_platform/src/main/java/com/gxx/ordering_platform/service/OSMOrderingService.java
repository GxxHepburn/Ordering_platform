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

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_OrderAdd_Tab_Tabtype_Orders;
import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.OrderAdd;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.Pay;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PayMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMOrderingService {
	
	@Autowired OrdersMapper ordersMapper;

	@Autowired WechatUserMapper wechatUserMapper;
	
	@Autowired MerMapper merMapper;
	
	@Autowired OrderAddMapper orderAddMapper;
	
	@Autowired OrderDetailMapper orderDetailMapper;
	
	@Autowired FoodMapper foodMapper;
	
	@Autowired MmaMapper mmaMapper;
	
	@Autowired PayMapper payMapper;
	
	@Transactional
	public String getOrderForm(Map<String, Object> map) {
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		
		Multi_Orders_Tab_Tabtype multi_Orders_Tab_Tabtype = ordersMapper.getOrderForm(O_ID);
		Mer mer = merMapper.getMerByMID(multi_Orders_Tab_Tabtype.getO_MID());
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		
		JSONObject orderJsonObject = new JSONObject(multi_Orders_Tab_Tabtype);
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if (multi_Orders_Tab_Tabtype.getO_PayTime() == null) {
			orderJsonObject.put("o_PayTime", "");
		} else {
			orderJsonObject.put("o_PayTime", simpleDateFormat.format(multi_Orders_Tab_Tabtype.getO_PayTime()));
		}
		
		orderJsonObject.put("o_OrderingTime", simpleDateFormat.format(multi_Orders_Tab_Tabtype.getO_OrderingTime()));
		dataJsonObject.put("orderForm", orderJsonObject);
		
		JSONObject merJsonObject = new JSONObject(mer);
		dataJsonObject.put("merForm", merJsonObject);
		
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
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
		
		int total = 0;
		// 根据参数
		// 订单号不为空，直接根据订单号，查询订单
		if (!"".equals(O_UniqSearchID)) {
			multi_Orders_Tab_Tabtypes = ordersMapper.getOrdersByUniqSearchIDOrderByIimeDESC(O_UniqSearchID);
			total = 1;
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
			total = ordersMapper.getOrdersTotalByUIDTabIDTabtypeIDOorderTimePayTime(U_ID,
					TabId, TabTypeId, datesList.get(0), datesList.get(1), datesList.get(2), datesList.get(3), PayStatus);
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("orderFormList", listToString(multi_Orders_Tab_Tabtypes));
		dataJsonObject.put("total", total);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	// 根据List<Multi_Orders_Tab_Tabtype> 返回值
	public JSONArray listToString(List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes) {
		
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
			jsonObject.put("O_NumberOfDiners", multi_Orders_Tab_Tabtype.getO_NumberOfDiners());
			
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
		
		return ordersJsonArray;
	}

	@Transactional
	public String getOrderAddFormList(Map<String, Object> map) {
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		
		List<OrderAdd> orderAdds = orderAddMapper.getByO_ID(O_ID);
		JSONArray orderAddsJsonArray = new JSONArray(orderAdds);
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (int i = 0; i < orderAdds.size(); i++) {
			List<OrderDetail> orderDetails = orderDetailMapper.getByOA_ID(orderAdds.get(i).getOA_ID());
			JSONArray orderDetailsJSONArray = new JSONArray(orderDetails);
			
			orderAddsJsonArray.getJSONObject(i).put("OA_OrderingTime", simpleDateFormat.format(orderAdds.get(i).getOA_OrderingTime()));
			
			orderAddsJsonArray.getJSONObject(i).put("orderDetails", orderDetailsJSONArray);
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("orderAddFormList", orderAddsJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@Transactional
	public String onlyReturnGoods(Map<String, Object> map) {
		// 因为是主动退点商品，所以不回复库存
		// 只update这个orderDetail,如果实际数目=0，则删除该orderdetail
		JSONObject sourceJsonObject = new JSONObject(map);
		JSONArray orderDetailJsonArray = sourceJsonObject.getJSONArray("onlyReturnGoodOrderDetailForm");
		int O_ID = sourceJsonObject.getInt("O_ID");
		for (int i = 0; i < orderDetailJsonArray.length(); i++) {
			int onlyReturnNum = orderDetailJsonArray.getJSONObject(i).getInt("onlyReturnNum");
			int OD_RealNum = orderDetailJsonArray.getJSONObject(i).getInt("OD_RealNum");
			int num = orderDetailJsonArray.getJSONObject(i).getInt("num");
			int OD_ID = orderDetailJsonArray.getJSONObject(i).getInt("OD_ID");
			int F_ID = orderDetailJsonArray.getJSONObject(i).getInt("id");
			if (onlyReturnNum > 0) {
				if (num -onlyReturnNum == 0) {
					// 删除该项
					orderDetailMapper.deleteByOD_ID(OD_ID);
				} else {
					// 更新realNum 和 num
					orderDetailMapper.updateRealNumAndNumByOD_ID(OD_ID, num- onlyReturnNum, OD_RealNum - onlyReturnNum);
				}
				// 商家主动退点，说明库存没了，所以将该商品库存设置为0
				foodMapper.updateStockByFID(0, F_ID);
			}
		}
		List<OrderAdd> orderAdds = orderAddMapper.getByO_ID(O_ID);
		for (OrderAdd orderAdd : orderAdds) {
			float nowOATotlePrice = 0.00f;
			List<OrderDetail> orderDetails = orderDetailMapper.getByOA_ID(orderAdd.getOA_ID());
			for (OrderDetail orderDetail : orderDetails) {
				nowOATotlePrice += orderDetail.getOD_RealPrice() * orderDetail.getOD_RealNum();
			}
			orderAddMapper.updateTotlePrice(orderAdd.getOA_ID(), nowOATotlePrice);
		}

		// 更新order ,orderadd 的总金额（不更新总数了，用不到的字段，让他乱吧)
		List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(O_ID);
		float nowOrderTotlePrice = 0.00f;
		for (OrderDetail orderDetail : orderDetails) {
			nowOrderTotlePrice += orderDetail.getOD_RealPrice() * orderDetail.getOD_RealNum();
		}
		ordersMapper.updateTotlePrice(O_ID, nowOrderTotlePrice);
		// 检查总订单金额是不是为0如果为0,则将订单状态改为未完成
		if (nowOrderTotlePrice == 0) {
			ordersMapper.updateO_PayStatueByO_ID(O_ID, 3);
		}

		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "仅退点餐品成功!");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String takingOrder(Map<String, Object> map) {
	
		int OA_ID = Integer.valueOf(map.get("OA_ID").toString());
		
		orderAddMapper.updateOA_IsTakingByOA_ID(OA_ID, "1");
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "接单成功!");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String notTakingOrerAddFormList (Map<String, Object> map) {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		
		List<Multi_OrderAdd_Tab_Tabtype_Orders> multi_OrderAdd_Tab_Tabtypes_Orderses = orderAddMapper.getNotTakingByMIDOrderByOrderingTime(m_ID, limitStart, pagesizeInt);
		
		int totle = orderAddMapper.getNotTakingTotleByMIDOrder(m_ID);
		
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
		dataJsonObject.put("totle", totle);
		
		newJsonObject.put("meta", metaJsonObject);
		newJsonObject.put("data", dataJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String orderFiUnderLine(Map<String, Object> map) {
		
		//在pay中插入一个空的支付记录用来标记线下支付
		int O_ID = Integer.valueOf(map.get("o_ID").toString());
		int O_MID = Integer.valueOf(map.get("o_MID").toString());
		int O_UID = Integer.valueOf(map.get("o_UID").toString());
		
		// 格式化P_Time_End时间
		SimpleDateFormat simpleDateFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
		String P_Time_End = simpleDateFormatParse.format(new Date());
		String P_Totle_Fee = String.valueOf(Integer.valueOf(map.get("o_TotlePrice").toString())*100);
		String P_Transaction_Id = "";
		String P_Trade_Type = "线下支付";
		String P_Bank_Type = "OTHERS";
		String P_Fee_Type = "CNY";
		
		Pay pay = new Pay();
		pay.setP_MID(O_MID);
		pay.setP_OID(O_ID);
		pay.setP_UID(O_UID);
		
		pay.setP_Time_End(P_Time_End);
		pay.setP_Totle_Fee(P_Totle_Fee);
		pay.setP_Transaction_Id(P_Transaction_Id);
		pay.setP_Trade_Type(P_Trade_Type);
		pay.setP_Bank_Type(P_Bank_Type);
		pay.setP_Fee_Type(P_Fee_Type);
		
		payMapper.insert(pay);
		
		
		// 更新orders O_PayStatue 
		ordersMapper.updateO_PayStatueByO_ID(O_ID, 1);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "标记线下支付成功!");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
