package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Multi_Refund_Orders_Tab_TabType;
import com.gxx.ordering_platform.entity.Refund;

public interface RefundMapper {

	@Insert("INSERT INTO refund (R_MID, R_UID, R_OID, R_PID, R_ORID, R_Is_OfLine, "
			+ "R_Transaction_Id, R_Out_Trade_No, R_Out_Refund_No, R_Refund_Id, R_Refund_Fee, "
			+ "R_Total_Fee, "
			+ "R_Submit_Time, R_Nonce_Str, R_Sign, R_Return_Msg, R_Mch_Id, R_Sub_Mch_Id, R_Cash_Fee, "
			+ "R_Coupon_Refund_Fee, R_Refund_Channel, R_Appid, R_Result_Code, R_Coupon_Refund_Count, "
			+ "R_Cash_Refund_Fee, R_Return_code) "
			+ "VALUES (#{refund.R_MID}, #{refund.R_UID}, #{refund.R_OID}, #{refund.R_PID}, "
			+ "#{refund.R_ORID}, #{refund.R_Is_OfLine}, "
			+ "#{refund.R_Transaction_Id}, #{refund.R_Out_Trade_No}, #{refund.R_Out_Refund_No}, "
			+ "#{refund.R_Refund_Id}, #{refund.R_Refund_Fee}, #{refund.R_Total_Fee}, "
			+ "#{refund.R_Submit_Time}, #{refund.R_Nonce_Str}, #{refund.R_Sign}, "
			+ "#{refund.R_Return_Msg}, #{refund.R_Mch_Id}, #{refund.R_Sub_Mch_Id}, #{refund.R_Cash_Fee}, "
			+ "#{refund.R_Coupon_Refund_Fee}, #{refund.R_Refund_Channel}, #{refund.R_Appid}, "
			+ "#{refund.R_Result_Code}, #{refund.R_Coupon_Refund_Count}, #{refund.R_Cash_Refund_Fee}, "
			+ "#{refund.R_Return_Code})")
	void insert(@Param("refund") Refund refund);
	
	@Select("SELECT * FROM refund WHERE R_Refund_Id = #{refund_id}")
	Refund getByRefund_id(@Param("refund_id") String refund_id);
	
	@Update("UPDATE refund SET R_Settlement_Total_Fee = #{settlement_total_fee}, "
			+ "R_Refund_Request_Source = #{refund_request_source}, "
			+ "R_Refund_Status = #{refund_status}, "
			+ "R_Settlement_Refund_Fee = #{settlement_refund_fee}, "
			+ "R_Success_Time = #{success_time}, "
			+ "R_Refund_Recv_Account = #{refund_recv_accout}, "
			+ "R_Refund_Account = #{refund_account} "
			+ "WHERE R_ID = #{r_id}")
	void updateReturnSuccess(@Param("settlement_total_fee") String settlement_total_fee, 
			@Param("refund_request_source") String refund_request_source, 
			@Param("refund_status") String refund_status, 
			@Param("settlement_refund_fee") String settlement_refund_fee, 
			@Param("success_time") String success_time, 
			@Param("refund_recv_accout") String refund_recv_accout, 
			@Param("refund_account") String refund_account, 
			@Param("r_id") int r_id);
	
	@Select("SELECT * FROM refund WHERE R_OID = #{o_id} ORDER BY R_Submit_Time")
	List<Refund> getByO_IdOrderByR_Submit_Time(@Param("o_id") int o_id);
	
	@Select("SELECT * FROM refund WHERE R_ID = #{r_id}")
	Refund getByR_ID(@Param("r_id") int r_id);
	
	@Select("SELECT * FROM refund WHERE R_Out_Refund_No = #{refundOutTradeNo}")
	Refund getByRefundOutTradeNo(@Param("refundOutTradeNo") String RefundOutTradeNo);
	
	@Select("SELECT * FROM refund WHERE R_Refund_Id = #{refundId}")
	Refund getByRefundId(@Param("refundId") String refundId);
	
	@Select("<script>"
			+ "SELECT * FROM refund left join orders on orders.O_ID = refund.R_OID left join tab on tab.T_ID = orders.O_TID "
			+ "left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE R_MID = #{m_id} "
			+ "<if test='u_id!=null'>"
			+ " AND R_UID = #{u_id}"
			+ "</if>"
			+ "<if test='o_id!=null'>"
			+ " AND R_OID = #{o_id}"
			+ "</if>"
			+ "<if test='refundOutTradeNo!=null'>"
			+ " AND R_Out_Refund_No = #{refundOutTradeNo}"
			+ "</if>"
			+ "<if test='refundTransactionId!=null'>"
			+ " AND R_Refund_Id = #{refundTransactionId}"
			+ "</if>"
			+ "<if test='tabId!=null'>"
			+ " AND orders.O_TID = #{tabId}"
			+ "</if>"
			+ "<if test='tabTypeId!=null'>"
			+ " AND tabtype.TT_ID = #{tabTypeId}"
			+ "</if>"
			+ "<if test='r_submit_start_time!=null'>"
			+ " AND R_Submit_Time &gt;= #{r_submit_start_time}"
			+ "</if>"
			+ "<if test='r_submit_end_time!=null'>"
			+ " AND R_Submit_Time &lt;= #{r_submit_end_time}"
			+ "</if>"
			+ "<if test='r_success_start_time!=null'>"
			+ " AND R_Success_Time &gt;= #{r_success_start_time}"
			+ "</if>"
			+ "<if test='r_success_end_time!=null'>"
			+ " AND R_Success_Time &lt;= #{r_success_end_time}"
			+ "</if>"
			+ " ORDER BY R_Submit_Time DESC"
			+ " limit #{limitStart}, #{pagesizeInt}"
			+ "</script>")
	List<Multi_Refund_Orders_Tab_TabType> getByUID_OID_RefundOutTradeNo_RefundId_RefundSubmitTime_RefundSuccessTime_TabId_TabTypeId(
			@Param("m_id") Integer m_id, @Param("u_id") Integer u_id, @Param("o_id") Integer o_id, @Param("refundOutTradeNo") String refundOutTradeNo, 
			@Param("refundTransactionId") String refundTransactionId, @Param("r_submit_start_time") String r_submit_start_time, 
			@Param("r_submit_end_time") String r_submit_end_time, @Param("r_success_start_time") String r_success_start_time, 
			@Param("r_success_end_time") String r_success_end_time, @Param("tabTypeId") Integer tabTypeId, 
			@Param("tabId") Integer tabId, @Param("limitStart") Integer limitStart, 
			@Param("pagesizeInt") Integer pagesizeInt);
	
	@Select("<script>"
			+ "SELECT COUNT(*) FROM refund left join orders on orders.O_ID = refund.R_OID left join tab on tab.T_ID = orders.O_TID "
			+ "left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE R_MID = #{m_id} "
			+ "<if test='u_id!=null'>"
			+ " AND R_UID = #{u_id}"
			+ "</if>"
			+ "<if test='o_id!=null'>"
			+ " AND R_OID = #{o_id}"
			+ "</if>"
			+ "<if test='refundOutTradeNo!=null'>"
			+ " AND R_Out_Refund_No = #{refundOutTradeNo}"
			+ "</if>"
			+ "<if test='refundTransactionId!=null'>"
			+ " AND R_Refund_Id = #{refundTransactionId}"
			+ "</if>"
			+ "<if test='tabId!=null'>"
			+ " AND orders.O_TID = #{tabId}"
			+ "</if>"
			+ "<if test='tabTypeId!=null'>"
			+ " AND tabtype.TT_ID = #{tabTypeId}"
			+ "</if>"
			+ "<if test='r_submit_start_time!=null'>"
			+ " AND R_Submit_Time &gt;= #{r_submit_start_time}"
			+ "</if>"
			+ "<if test='r_submit_end_time!=null'>"
			+ " AND R_Submit_Time &lt;= #{r_submit_end_time}"
			+ "</if>"
			+ "<if test='r_success_start_time!=null'>"
			+ " AND R_Success_Time &gt;= #{r_success_start_time}"
			+ "</if>"
			+ "<if test='r_success_end_time!=null'>"
			+ " AND R_Success_Time &lt;= #{r_success_end_time}"
			+ "</if>"
			+ "</script>")
	int getRefundTotalByUID_OID_RefundOutTradeNo_RefundId_RefundSubmitTime_RefundSuccessTime_TabId_TabTypeId(@Param("m_id") Integer m_id, @Param("u_id") Integer u_id, @Param("o_id") Integer o_id, @Param("refundOutTradeNo") String refundOutTradeNo, 
			@Param("refundTransactionId") String refundTransactionId, @Param("r_submit_start_time") String r_submit_start_time, 
			@Param("r_submit_end_time") String r_submit_end_time, @Param("r_success_start_time") String r_success_start_time, 
			@Param("r_success_end_time") String r_success_end_time, @Param("tabTypeId") Integer tabTypeId, 
			@Param("tabId") Integer tabId);
}
