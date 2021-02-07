package com.gxx.ordering_platform.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.Refund;
import com.gxx.ordering_platform.mapper.RefundMapper;

@Component
public class OSMRefundService {
	
	@Autowired 
	RefundMapper refundMapper;
	
	@Autowired
	WxPayService wxPayService;

	@Transactional
	public String getRefundFormList (Map<String, Object> map) {
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		List<Refund> refunds = refundMapper.getByO_IdOrderByR_Submit_Time(O_ID);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat parseDate = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		
		
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 200);
		metaJsonObject.put("msg", "获取成功");
		
		JSONObject dataJsonObject = new JSONObject();
		JSONArray refundsJsonArray = new JSONArray();
		for (int i = 0; i < refunds.size(); i++) {
			JSONObject refundItemJsonObject = new JSONObject();
			
			// mybatis 对于字段为空的string映射为字符串"null"
			Refund refund = refunds.get(i);
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
			try {
				// 该字段如果为空，前端也自动为空
				refundItemJsonObject.put("R_Submit_Time", format.format(parseDate.parse(refund.getR_Submit_Time())));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
}
