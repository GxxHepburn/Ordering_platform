package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
	Refund getByRefund_id(@Param("refund_id") String refdund_id);
	
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
}
