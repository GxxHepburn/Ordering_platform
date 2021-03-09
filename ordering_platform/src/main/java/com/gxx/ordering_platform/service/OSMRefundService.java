package com.gxx.ordering_platform.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.BankType;
import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_Pay_Orders_Tab_TabType;
import com.gxx.ordering_platform.entity.Multi_Refund_Orders_Tab_TabType;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.RC;
import com.gxx.ordering_platform.entity.RS;
import com.gxx.ordering_platform.entity.Refund;
import com.gxx.ordering_platform.entity.ReturnOrdersPTimes;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrderReturnMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.RefundMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.utils.EncryptionAndDeciphering;

@Component
public class OSMRefundService {
	
	@Autowired 
	RefundMapper refundMapper;
	
	@Autowired
	WxPayService wxPayService;
	
	@Autowired
	MmaMapper mmaMapper;
	
	@Autowired
	WechatUserMapper wechatUserMapper;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	OrderReturnMapper orderReturnMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	

	@Transactional
	public String getRefundFormList (Map<String, Object> map) {
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		List<Refund> refunds = refundMapper.getByO_IdOrderByR_Submit_Time(O_ID);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray refundsJsonArray = new JSONArray();
		for (int i = 0; i < refunds.size(); i++) {

			Refund refund = refunds.get(i);
			// 判断是否需要查询
			if (refund.getR_Is_OfLine() == 1 && refund.getR_Refund_Status() == null) {
				// 查询同时更新到数据库中
				long R_Submit_Time = 0l;
				try {
					R_Submit_Time = format.parse(refund.getR_Submit_Time()).getTime();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int hours = 72;
				int secondsOfHour = 60 * 60;
				int millisOfSeconde = 1000;
				/*hours * secondsOfHour * millisOfSeconde*/
				if (R_Submit_Time + hours * secondsOfHour * millisOfSeconde < new Date().getTime()) {
					Map<String, String> resultap = null;
					try {
						resultap = wxPayService.refundQuery(refund);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 更新数据库
					String settlement_total_fee = resultap.get("settlement_total_fee");
					String refund_request_source = resultap.get("refund_request_source");
					String refund_status = resultap.get("refund_status_0");
					String settlement_refund_fee = resultap.get("settlement_refund_fee");
					String success_time = resultap.get("refund_success_time_0");
					String refund_recv_accout =  resultap.get("refund_recv_accout_0");
					String refund_account = resultap.get("refund_account_0");
					
					refundMapper.updateReturnSuccess(settlement_total_fee, refund_request_source, 
							refund_status, settlement_refund_fee, success_time, refund_recv_accout, 
							refund_account, refund.getR_ID());
					// 替换refund
					refund.setR_Settlement_Total_Fee(settlement_total_fee);
					refund.setR_Refund_Request_Source(refund_request_source);
					refund.setR_Refund_Status(refund_status);
					refund.setR_Settlement_Refund_Fee(settlement_refund_fee);
					refund.setR_Success_Time(success_time);
					refund.setR_Refund_Recv_Account(refund_recv_accout);
					refund.setR_Refund_Account(refund_account);
				}
			}
			
			JSONObject refundItemJsonObject = new JSONObject();
			
			// mybatis 对于字段为空的string映射为字符串"null"
			refundItemJsonObject.put("R_ID", refund.getR_ID());
			refundItemJsonObject.put("R_MID", refund.getR_MID());
			refundItemJsonObject.put("R_UID", refund.getR_UID());
			refundItemJsonObject.put("R_OID", refund.getR_OID());
			refundItemJsonObject.put("R_PID", refund.getR_PID());
			refundItemJsonObject.put("R_ORID", refund.getR_ORID());
			refundItemJsonObject.put("R_Is_OfLine", refund.getR_Is_OfLine());
			
			refundItemJsonObject.put("R_Transaction_Id", refund.getR_Transaction_Id());
			refundItemJsonObject.put("R_Out_Trade_No", refund.getR_Out_Trade_No());
			refundItemJsonObject.put("R_Out_Refund_No", refund.getR_Out_Refund_No());
			refundItemJsonObject.put("R_Refund_Id", refund.getR_Refund_Id());
			refundItemJsonObject.put("R_Refund_Fee", refund.getR_Refund_Fee());
			refundItemJsonObject.put("R_Total_Fee", refund.getR_Total_Fee());
			refundItemJsonObject.put("R_Submit_Time", refund.getR_Submit_Time());
			refundItemJsonObject.put("R_Return_Msg", refund.getR_Return_Msg());
			refundItemJsonObject.put("R_Result_Code", refund.getR_Result_Code());
			refundItemJsonObject.put("R_Return_Code", refund.getR_Return_Code());
			refundItemJsonObject.put("R_Success_Time", refund.getR_Success_Time());
			refundItemJsonObject.put("R_Refund_Status", refund.getR_Refund_Status());
			refundItemJsonObject.put("R_Refund_Recv_Account", refund.getR_Refund_Recv_Account());
			
			refundsJsonArray.put(refundItemJsonObject);
		}
		dataJsonObject.put("refundFormList", refundsJsonArray);
		
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String refundQuery (Map<String, Object> map) {
		int R_ID = Integer.valueOf(map.get("R_ID").toString());
		Refund refund = refundMapper.getByR_ID(R_ID);
		
		// 先判断是否需要查询
		if (refund.getR_Refund_Status() == null) {
			Map<String, String> resultap = null;
			try {
				resultap = wxPayService.refundQuery(refund);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException();
			}
			String settlement_total_fee = resultap.get("settlement_total_fee");
			String refund_request_source = resultap.get("refund_request_source");
			String refund_status = resultap.get("refund_status_0");
			String settlement_refund_fee = resultap.get("settlement_refund_fee");
			String success_time = resultap.get("refund_success_time_0");
			String refund_recv_accout =  resultap.get("refund_recv_accout_0");
			String refund_account = resultap.get("refund_account_0");
			
			refundMapper.updateReturnSuccess(settlement_total_fee, refund_request_source, 
					refund_status, settlement_refund_fee, success_time, refund_recv_accout, 
					refund_account, refund.getR_ID());
		}
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "查询退款到账成功");
		
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String getRefundRecordFormList(Map<String, Object> map) {
		
		int pagenumInt = Integer.valueOf(map.get("pagenum").toString());
		int pagesizeInt = Integer.valueOf(map.get("pagesize").toString());
		int limitStart = (pagenumInt - 1) * pagesizeInt;
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		// 先进行空值判断过滤
		String O_UniqSearchID = map.get("O_UniqSearchID").toString();
		String U_OpenId = map.get("U_OpenId").toString();
		String refundTransactionId = map.get("RefundTransactionId").toString();
		String refundOutTradeNo = map.get("RefundOutTradeNo").toString();
		if ("".equals(refundTransactionId)) {
			refundTransactionId = null;
		}
		if ("".equals(refundOutTradeNo)) {
			refundOutTradeNo = null;
		}
		
		Integer U_ID = null;
		Integer O_ID = null;
		
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
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
		SimpleDateFormat formatSuccessString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		String refundSubmitStartTimeString = null;
		String refundSubmitEndTimeString = null;
		String refundSuccessStartTimeString = null;
		String refundSuccessEndTimeString = null;
		
		List<String> datesStringList = new ArrayList<String>();
		datesStringList.add(refundSubmitStartTimeString);
		datesStringList.add(refundSubmitEndTimeString);
		datesStringList.add(refundSuccessStartTimeString);
		datesStringList.add(refundSuccessEndTimeString);
		
		
		List<String> timeStringsList = new ArrayList<String>();
		
		String refundSubmitStartTime = "";
		if (map.get("RefundSubmitStartTime") != null) {
			refundSubmitStartTime = map.get("RefundSubmitStartTime").toString();
		}
		String refundSubmitEndTime = "";
		if (map.get("RefundSubmitEndTime") != null) {
			refundSubmitEndTime = map.get("RefundSubmitEndTime").toString();
		}
		String refundSuccessStartTime = "";
		if (map.get("RefundSuccessStartTime") != null) {
			refundSuccessStartTime = map.get("RefundSuccessStartTime").toString();
		}
		String refundSuccessEndTime = "";
		if (map.get("RefundSuccessEndTime") != null) {
			refundSuccessEndTime = map.get("RefundSuccessEndTime").toString();
		}
		
		timeStringsList.add(refundSubmitStartTime);
		timeStringsList.add(refundSubmitEndTime);
		timeStringsList.add(refundSuccessStartTime);
		timeStringsList.add(refundSuccessEndTime);
		
		for(int i = 0; i < timeStringsList.size(); i++) {
			if (!"".equals(timeStringsList.get(i))) {
				// 处理下单开始时间
				String newTimeString = timeStringsList.get(i).replace("Z", " UTC");
				
				try {
					datesStringList.set(i, formatSuccessString.format(new Date(format.parse(newTimeString).getTime())));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if (!"".equals(U_OpenId)) {
			// 有商户号
			// 获得O_UID
			try {
				String real_U_OpenId = EncryptionAndDeciphering.deciphering(U_OpenId);
				WechatUser wechatUser = wechatUserMapper.getByUOpenId(real_U_OpenId);
				U_ID = wechatUser.getU_ID();
			} catch (Exception e) {
				logger.info("用户号解密错误!");
				U_ID = 0;
			}
		}
		
		if (!"".equals(O_UniqSearchID)) {
			// 有商户号
			// 获得O_ID
			try {
				Orders orders = ordersMapper.getOrdersByUniqSearchID(O_UniqSearchID);
				O_ID = orders.getO_ID();
			} catch (Exception e) {
				logger.info("订单号错误!");
				O_ID = 0;
			}
		}
		
		List<Multi_Refund_Orders_Tab_TabType> multi_Refund_Orders_Tab_TabTypes = null;
		int total = 0;
		
		//TODO 从数据库中检索
		multi_Refund_Orders_Tab_TabTypes = refundMapper.getByUID_OID_RefundOutTradeNo_RefundId_RefundSubmitTime_RefundSuccessTime_TabId_TabTypeId(
				m_ID, U_ID, O_ID, refundOutTradeNo, refundTransactionId, datesStringList.get(0), datesStringList.get(1), datesStringList.get(2), datesStringList.get(3), 
				TabTypeId, TabId, limitStart, pagesizeInt);
		total = refundMapper.getRefundTotalByUID_OID_RefundOutTradeNo_RefundId_RefundSubmitTime_RefundSuccessTime_TabId_TabTypeId(
				m_ID, U_ID, O_ID, refundOutTradeNo, refundTransactionId, datesStringList.get(0), datesStringList.get(1), datesStringList.get(2), datesStringList.get(3),  
				TabTypeId, TabId);
		
		JSONArray refundJsonArray = new JSONArray(multi_Refund_Orders_Tab_TabTypes);
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("refundFormList", refundJsonArray);
		dataJsonObject.put("total", total);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
	
	@Transactional
	public String searchRefundPMonth(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = map.get("mmngctUserName").toString();
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String yearString = map.get("refundYear").toString();
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date dateStart = simpleDateFormat.parse(yearString + "-01-01 00:00:00");
		Date dateEnd = simpleDateFormat.parse(yearString + "-12-31 23:59:59");
		
		List<ReturnOrdersPTimes> returnOrdersPMonths = orderReturnMapper.searchReturnOrdersPMonth(m_ID, dateStart, dateEnd);
		List<ReturnOrdersPTimes> newReturnOrdersPMonths = new ArrayList<ReturnOrdersPTimes>();
		
		int j = 0;
		for (int i = 0; i < 12;) {
			i++;
			String nowTimeString = yearString + "-" + (i < 10 ? "0" + i : i);
			if (j < returnOrdersPMonths.size() && nowTimeString.equals(returnOrdersPMonths.get(j).getTimes())) {
				ReturnOrdersPTimes returnOrdersPMonth = returnOrdersPMonths.get(j);
				returnOrdersPMonth.setTimes(i + "月");
				newReturnOrdersPMonths.add(returnOrdersPMonth);
				j++;
			} else {
				ReturnOrdersPTimes returnOrdersPMonth = new ReturnOrdersPTimes();
				returnOrdersPMonth.setTimes(i + "月");
				newReturnOrdersPMonths.add(returnOrdersPMonth);
			}
		}
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("refundMonthFormList", new JSONArray(newReturnOrdersPMonths));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String searchRCFormList(Map<String, Object> map) throws Exception {
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String RCStartString = map.get("RCStartString").toString();
		String RCEndString = map.get("RCEndString").toString();
		
		String RCO_UniqSearchIDString = map.get("RCO_UniqSearchID").toString();
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date RCStartDate = format.parse(RCStartString);
		Date RCEndDate = format.parse(RCEndString);
		
		String RCO_UniqSearchID = null;
		
		if (!"".equals(RCO_UniqSearchIDString)) {
			RCO_UniqSearchID = RCO_UniqSearchIDString;
		}
		
		// 根据有无订单号，分别检索，有订单号，无视时间，无订单号，则只用时间作为条件
		List<RC> rcs = null;
		if (RCO_UniqSearchID == null) {
			rcs = orderReturnMapper.searchRCWithoutOUID(m_ID, RCStartDate, RCEndDate);
		} else {
			rcs = orderReturnMapper.searchRCWithOUID(m_ID, RCO_UniqSearchID);
		}
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONArray rcsJsonArray = new JSONArray(rcs);
		for (int i = 0; i < rcsJsonArray.length(); i++) {
			rcsJsonArray.getJSONObject(i).put("orderTime", format.format(rcs.get(i).getOrderTime()));
			rcsJsonArray.getJSONObject(i).put("returnTime", format.format(rcs.get(i).getReturnTime()));
		}
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("RCFormList", rcsJsonArray);
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}

	@Transactional
	public String searchRSFormList(Map<String, Object> map) throws Exception {
		
		String mmngctUserName = (String) map.get("mmngctUserName");
		
		//根据mmngctUserName查出merId
		Mmngct mmngct = mmaMapper.getByUsername(mmngctUserName);
		int m_ID = mmngct.getMMA_ID();
		
		String RSStartString = map.get("RSStartString").toString();
		String RSEndString = map.get("RSEndString").toString();
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		Date RSStartDate = format.parse(RSStartString);
		Date RSEndDate = format.parse(RSEndString);
		
		List<RS> rses = orderReturnMapper.searchRS(m_ID, RSStartDate, RSEndDate);
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		
		JSONObject dataJsonObject = new JSONObject();
		dataJsonObject.put("RSFormList", new JSONArray(rses));
		
		newJsonObject.put("data", dataJsonObject);
		newJsonObject.put("meta", metaJsonObject);
		
		return newJsonObject.toString();
	}
}
