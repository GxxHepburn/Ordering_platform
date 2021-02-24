package com.gxx.ordering_platform.service;

import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_OrderAdd_Tab_Tabtype_Orders;
import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.OrderAdd;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.OrderReturn;
import com.gxx.ordering_platform.entity.OrderReturnDetail;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Pay;
import com.gxx.ordering_platform.entity.Refund;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.FoodMapper;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrderReturnDetailMapper;
import com.gxx.ordering_platform.mapper.OrderReturnMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PayMapper;
import com.gxx.ordering_platform.mapper.RefundMapper;
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
	
	@Autowired OrderReturnMapper orderReturnMapper;
	
	@Autowired OrderReturnDetailMapper orderReturnDetailMapper;
	
	@Autowired WxPayService wxPayService;
	
	@Autowired RefundMapper refundMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
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
		int touchButton = Integer.valueOf(map.get("touchButton").toString());
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = null;
		
		int total = 0;
		
		// 按照各种条件组合检索
		if (touchButton == 1) {
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
		}
		
		// 检索商户单号所属订单
		if (touchButton == 2) {
			String outTradeNo = map.get("OutTradeNo").toString();
			Pay pay = payMapper.getByO_OutTrade_No(outTradeNo);
			Integer O_ID = null;
			if (pay == null) {
				if (!"".equals(outTradeNo)) {
					O_ID = 0;
				}
			} else {
				O_ID = pay.getP_OID();
			}
			
			multi_Orders_Tab_Tabtypes = ordersMapper.getMulti_Orders_Tab_TabtypesByO_ID(O_ID);
			
			total = ordersMapper.getmMulti_Orders_Tab_TabtypesTotalByO_ID(O_ID);
		}
		
		// 检索支付单号所属订单
		if (touchButton == 3) {
			String transactionId = map.get("TransactionId").toString();
			Pay pay = payMapper.getByTransactionId(transactionId);
			Integer O_ID = null;
			if (pay == null) {
				if (!"".equals(transactionId)) {
					O_ID = 0;
				}
			} else {
				O_ID = pay.getP_OID();
			}
			
			multi_Orders_Tab_Tabtypes = ordersMapper.getMulti_Orders_Tab_TabtypesByO_ID(O_ID);
			
			total = ordersMapper.getmMulti_Orders_Tab_TabtypesTotalByO_ID(O_ID);
		}
		
		// 检索商户退款单号所属订单
		if (touchButton == 4) {
			String refundOutTradeNo = map.get("RefundOutTradeNo").toString();
			Refund refund = refundMapper.getByRefundOutTradeNo(refundOutTradeNo);
			Integer O_ID = null;
			if (refund == null) {
				if (!"".equals(refundOutTradeNo)) {
					O_ID = 0;
				}
			} else {
				O_ID = refund.getR_OID();
			}
			
			multi_Orders_Tab_Tabtypes = ordersMapper.getMulti_Orders_Tab_TabtypesByO_ID(O_ID);
			
			total = ordersMapper.getmMulti_Orders_Tab_TabtypesTotalByO_ID(O_ID);
		}
		
		// 检索退款单号所属订单
		if (touchButton == 5) {
			String refundId = map.get("RefundId").toString();
			Refund refund = refundMapper.getByRefundId(refundId);
			Integer O_ID = null;
			if (refund == null) {
				if (!"".equals(refundId)) {
					O_ID = 0;
				}
			} else {
				O_ID = refund.getR_OID();
			}
			
			multi_Orders_Tab_Tabtypes = ordersMapper.getMulti_Orders_Tab_TabtypesByO_ID(O_ID);
			
			total = ordersMapper.getmMulti_Orders_Tab_TabtypesTotalByO_ID(O_ID);
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

		int O_ID = Integer.valueOf(map.get("o_ID").toString());
		// 线检查O_isPayNow字段
		Orders orders = ordersMapper.getordersByO_ID(O_ID);
		if (orders.getO_IsPayNow() == 1) {
			JSONObject newJsonObject = new JSONObject();
			
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 0);
			metaJsonObject.put("msg", "客户正在付款，请重试!");
			
			newJsonObject.put("meta", metaJsonObject);
			
			return newJsonObject.toString();
		}
		
		
		//在pay中插入一个空的支付记录用来标记线下支付
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
	
	@Transactional
	public String orderNotFiUnderLine(Map<String, Object> map) {

		int O_ID = Integer.valueOf(map.get("o_ID").toString());
		// 线检查O_isPayNow字段
		Orders orders = ordersMapper.getordersByO_ID(O_ID);
		if (orders.getO_IsPayNow() == 1) {
			JSONObject newJsonObject = new JSONObject();
			
			JSONObject metaJsonObject = new JSONObject();
			metaJsonObject.put("status", 0);
			metaJsonObject.put("msg", "客户正在付款，请重试!");
			
			newJsonObject.put("meta", metaJsonObject);
			
			return newJsonObject.toString();
		}
		
		
		// 更新orders O_PayStatue 
		ordersMapper.updateO_PayStatueByO_ID(O_ID, 3);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "标记订单未完成成功!");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String returnGoodsWithMoney(Map<String, Object> map) {
		
		JSONObject sourceJsonObject = new JSONObject(map);
		JSONArray returnGoodWithMoneyOrderDetailFormJsonArray = sourceJsonObject.getJSONArray("returnGoodWithMoneyOrderDetailForm");
		int O_ID = sourceJsonObject.getInt("O_ID");
		Orders orders = ordersMapper.getordersByO_ID(O_ID);
		//插入一个orderReturn，先获取sort，然后插入，最后更新退款总价
		OrderReturn orderReturn = new OrderReturn();
		int count = orderReturnMapper.selectCountByOR_OID(O_ID);
		orderReturn.setOR_OID(O_ID);
		orderReturn.setOR_Sort(1 + count);
		orderReturn.setOR_MID(orders.getO_MID());
		orderReturn.setOR_UID(orders.getO_UID());
		orderReturn.setOR_TID(orders.getO_TID());
		//OR_TotlePrice,等会更新
		orderReturn.setOR_ReturnTime(new Date());
		orderReturnMapper.insert(orderReturn);
		
		// 插入orderReturnDetail
		
		JSONArray newJsonArray = new JSONArray();
		// 将所有的orderDetailJSONArray合并到一起
		for (int i = 0; i < returnGoodWithMoneyOrderDetailFormJsonArray.length(); i++) {
//			newJsonArray.(returnGoodWithMoneyOrderDetailFormJsonArray.getJSONObject(i).getJSONArray("orderDetails"));
			for (int j = 0; j < returnGoodWithMoneyOrderDetailFormJsonArray.getJSONObject(i).getJSONArray("orderDetails").length(); j++) {
				newJsonArray.put(returnGoodWithMoneyOrderDetailFormJsonArray.getJSONObject(i).getJSONArray("orderDetails").get(j));
			}
		}
		float nowORTotlePrice = 0.00f;
		// 修改orderDetail
		for (int i = 0; i < newJsonArray.length(); i++) {
			int onlyReturnNum = newJsonArray.getJSONObject(i).getInt("returnNum");
			int OD_RealNum = newJsonArray.getJSONObject(i).getInt("OD_RealNum");
			int num = newJsonArray.getJSONObject(i).getInt("OD_Num");
			int OD_ID = newJsonArray.getJSONObject(i).getInt("OD_ID");
			int F_ID = newJsonArray.getJSONObject(i).getInt("OD_FID");
			if (onlyReturnNum > 0) {
				// 更新realNum 和 num
				orderDetailMapper.updateRealNumAndNumByOD_ID(OD_ID, num, OD_RealNum - onlyReturnNum);
				// 商家主动退点，说明库存没了，所以将该商品库存设置为0-------这一条不适用于退款，只适用于仅退点商品
//				foodMapper.updateStockByFID(0, F_ID);
				// 插入orderReturnDetail
				OrderReturnDetail orderReturnDetail = new OrderReturnDetail();
				orderReturnDetail.setORD_ORID(orderReturn.getOR_ID());
				orderReturnDetail.setORD_OID(orderReturn.getOR_OID());
				orderReturnDetail.setORD_FID(newJsonArray.getJSONObject(i).getInt("OD_FID"));
				orderReturnDetail.setORD_FoodState(newJsonArray.getJSONObject(i).getInt("OD_FoodState"));
				orderReturnDetail.setORD_RealPrice(newJsonArray.getJSONObject(i).getFloat("OD_RealPrice"));
				orderReturnDetail.setORD_Spec(newJsonArray.getJSONObject(i).getString("OD_Spec"));
				orderReturnDetail.setORD_PropOne(newJsonArray.getJSONObject(i).getString("OD_PropOne"));
				orderReturnDetail.setORD_PropTwo(newJsonArray.getJSONObject(i).getString("OD_PropTwo"));
				orderReturnDetail.setORD_Num(newJsonArray.getJSONObject(i).getInt("returnNum"));
				orderReturnDetail.setORD_FName(newJsonArray.getJSONObject(i).getString("OD_FName"));
				nowORTotlePrice += orderReturnDetail.getORD_RealPrice() * orderReturnDetail.getORD_Num();
				// 插入orderReturnDetail
				orderReturnDetailMapper.insert(orderReturnDetail);
			}
		}
		// 修改orderreturn总价
		orderReturnMapper.updateTotlePrice(orderReturn.getOR_ID(), nowORTotlePrice);
		
		// 修改orderadd总价
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
		
		
		// 还是应该将其列入退款项目中去
		ordersMapper.updateO_PayStatueByO_ID(O_ID, 2);
		
		// 先判断，是不是在线支付，如果是，则向微信请求，如果不是，则直接返回，提示商家，这是线下支付，请商家人工退款
		Pay pay = payMapper.getByO_ID(O_ID);
		String msg = "";
		int status = 200;
		// 初始化退款表
		Refund refund = new Refund();
		refund.setR_MID(pay.getP_MID());
		refund.setR_UID(pay.getP_UID());
		refund.setR_OID(pay.getP_OID());
		refund.setR_PID(pay.getP_ID());
		refund.setR_ORID(orderReturn.getOR_ID());
		
		// 修改提交时间格式，搞成yyyy-MM-dd hh:MM:ss
		SimpleDateFormat refundSubmitTimeDateFormatParse = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		refund.setR_Submit_Time(refundSubmitTimeDateFormatParse.format(new Date()));
		
		if ("线下支付".equals(pay.getP_Trade_Type())) {
			msg = "退菜成功！本单为线下支付，请尽快人工退款！";
			// 设置退款
			refund.setR_Is_OfLine(0);
		} else {
			// 根据支付时间，确定能不能退款
			// 格式化P_Time_End时间
			SimpleDateFormat simpleDateFormatParse = new SimpleDateFormat("yyyyMMddHHmmss");
			Date P_Time_End_Date = null;
			try {
				P_Time_End_Date = simpleDateFormatParse.parse(pay.getP_Time_End());
			} catch (Exception e) {
				logger.info("支付时间null");
			}
			Date nowDate = new Date();
			
			long timeDiff = nowDate.getTime() - P_Time_End_Date.getTime();
			int timeHour = 6;
			int minutesOneHour = 60;
			int secondesOneHour = 60;
			int millisecondedOneSecond = 1000;
			
			if (timeDiff > timeHour*minutesOneHour*secondesOneHour*millisecondedOneSecond) {
				// 超过系统允许退款最大时间6小时
				msg = "退菜成功！本单超过系统允许退款最大时间6小时，请人工退款！";
				// 设置退款
				refund.setR_Is_OfLine(0);
				
				// 插入退款记录,在returnSuccess中update退款记录
				try {
					refundMapper.insert(refund);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					msg = "退菜成功！本单超过系统允许退款最大时间6小时，请人工退款！插入退款记录失败！请联系管理员！";
					e.printStackTrace();
				}
				
				JSONObject newJsonObject = new JSONObject();
				
				JSONObject metaJsonObject = new JSONObject();
				metaJsonObject.put("status", status);
				metaJsonObject.put("msg", msg);
				
				newJsonObject.put("meta", metaJsonObject);
				
				return newJsonObject.toString();		
			}
			// 向微信支付申请退款,然后后台根据是否成功提交，来显示
			// 如果成功提交，则该退款信息显示，提交退款成功，请稍后查看退款详情
			// 如果提交退款失败，则根据具体原因提示商家，退款失败，转为人工退款，需要商家核实之后，自行转账！
			
			//准备退款参数
			String out_refund_no = UUID.randomUUID().toString().replaceAll("-", "");
			String out_trade_no = pay.getP_Out_Trade_No();
			String totle_fee = pay.getP_Totle_Fee();
			int refund_fee_int = (int)(nowORTotlePrice*100);
			String refund_fee = String.valueOf(refund_fee_int);
			Map<String, String> resultMap = null;
			try {
				resultMap = wxPayService.returnMoneyFromWechat(out_trade_no, out_refund_no, totle_fee, refund_fee);
			} catch (Exception exception) {
				exception.printStackTrace();
				msg = "退菜成功！提交退款失败，请人工退款！";
				refund.setR_Is_OfLine(0);
				status = 201;
				
				// 插入退款记录,在returnSuccess中update退款记录
				try {
					refundMapper.insert(refund);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					msg = "退菜成功！提交退款失败，请人工退款！插入退款记录失败！请联系管理员！";
					e.printStackTrace();
				}
				
				JSONObject newJsonObject = new JSONObject();
				
				JSONObject metaJsonObject = new JSONObject();
				metaJsonObject.put("status", status);
				metaJsonObject.put("msg", msg);
				
				newJsonObject.put("meta", metaJsonObject);
				
				return newJsonObject.toString();
			}
			
			if ("FAIL".equals(resultMap.get("result_code"))) {
				// 提交失败
				msg = "退菜成功！提交退款失败，请人工退款! 错误原因:" + resultMap.get("err_code_des");
				refund.setR_Is_OfLine(0);
				status = 201;
				
			} else {
				msg = "退菜成功！提交退款成功！";
				
				refund.setR_Is_OfLine(1);
				refund.setR_Transaction_Id(resultMap.get("transaction_id"));
				refund.setR_Out_Trade_No(resultMap.get("out_trade_no"));
				refund.setR_Out_Refund_No(resultMap.get("out_refund_no"));
				refund.setR_Refund_Id(resultMap.get("refund_id"));
				refund.setR_Refund_Fee(resultMap.get("refund_fee"));
				refund.setR_Total_Fee(resultMap.get("total_fee"));
				
				refund.setR_Nonce_Str(resultMap.get("nonce_str"));
				refund.setR_Sign(resultMap.get("sign"));
				refund.setR_Return_Msg(resultMap.get("return_msg"));
				refund.setR_Mch_Id(resultMap.get("mch_id"));
				refund.setR_Sub_Mch_Id(resultMap.get("sub_mch_id"));
				refund.setR_Cash_Fee(resultMap.get("cash_fee"));
				refund.setR_Coupon_Refund_Fee(resultMap.get("coupon_refund_fee"));
				refund.setR_Refund_Channel(resultMap.get("refund_channel"));
				refund.setR_Appid(resultMap.get("appid"));
				refund.setR_Result_Code(resultMap.get("result_code"));
				refund.setR_Coupon_Refund_Count(resultMap.get("coupon_refund_count"));
				refund.setR_Cash_Refund_Fee(resultMap.get("cash_refund_fee"));
				refund.setR_Return_Code(resultMap.get("return_code"));
			}
		}
		// 插入退款记录,在returnSuccess中update退款记录
		try {
			refundMapper.insert(refund);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			msg = msg + "插入退款记录失败!请联系管理员!";
			e.printStackTrace();
		}

		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", status);
		metaJsonObject.put("msg", msg);
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String getOrderReturnFormList(Map<String, Object> map) {
		
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		
		List<OrderReturn> orderReturns = orderReturnMapper.getByO_ID(O_ID);
		JSONArray orderReturnsJsonArray = new JSONArray(orderReturns);
		
		//格式化时间
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		for (int i = 0; i <orderReturns.size(); i++) {
			List<OrderReturnDetail> orderReturnDetails = orderReturnDetailMapper.getByOR_ID(orderReturns.get(i).getOR_ID());
			JSONArray orderReturnDetailsJsonArray = new JSONArray(orderReturnDetails);
			
			orderReturnsJsonArray.getJSONObject(i).put("OR_ReturnTime", simpleDateFormat.format(orderReturns.get(i).getOR_ReturnTime()));
			orderReturnsJsonArray.getJSONObject(i).put("orderReturnDetails", orderReturnDetailsJsonArray);
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("orderReturnFormList", orderReturnsJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@Transactional
	public String getLastOrderFormList(Map<String, Object> map) {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		// 获取最近24小时订单
		Date nowDate = new Date();
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(nowDate);
		calendar.add(Calendar.DATE, -1);
		Date lastDate = calendar.getTime();
		
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = null;
		int total = 0;
		multi_Orders_Tab_Tabtypes = ordersMapper.getLastOrdersByMIDDESC(m_ID, lastDate, limitStart, pagesizeInt);
		total = ordersMapper.getLastOrdersTotal(m_ID, lastDate);
		
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
	
	@Transactional
	public String getNotPayReturnAndNotFiAndFiOrderFormList(Map<String, Object> map) {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		int timeStatus = Integer.valueOf(map.get("timeStatus").toString());
		
		int payStatus = Integer.valueOf(map.get("payStatus").toString());
		
		Date requireDate = null;
		Date nowDate = new Date();
		Calendar calendar = new GregorianCalendar();
		
		// 处理时间
		if (timeStatus == 0) {
			long requireTimeStamp = 0l;
			requireDate = new Date(requireTimeStamp);
		}
		
		if (0 < timeStatus && timeStatus <= 3) {
			calendar.setTime(nowDate);
			calendar.add(Calendar.DATE, -timeStatus);
			requireDate = calendar.getTime();
		}
		
		if (timeStatus == 4) {
			calendar.setTime(nowDate);
			calendar.add(Calendar.DATE, -7);
			requireDate = calendar.getTime();
		}
		
		if (timeStatus == 5) {
			calendar.setTime(nowDate);
			calendar.add(Calendar.DATE, -30);
			requireDate = calendar.getTime();
		}
		List<Multi_Orders_Tab_Tabtype> multi_Orders_Tab_Tabtypes = null;
		int total = 0;
		multi_Orders_Tab_Tabtypes = ordersMapper.getNotPayReturnAndNotFiAndFiOrdersByMIDANDOrderingTimeDESC(m_ID, requireDate, payStatus, limitStart, pagesizeInt);
		total = ordersMapper.getNReturnAndNotFiAndFiOrdersTotalByMIDANDOrderingTime(m_ID, requireDate, payStatus);
		
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
}
