package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Pay;

public interface PayMapper {

	@Insert("INSERT INTO pay (P_MID, P_OID, P_UID, P_Appid, "
			+ "P_Attach, P_Bank_Type, P_Fee_Type, P_Is_Subscribe, "
			+ "P_Mch_Id, P_Nonce_Str, P_Openid, P_Out_Trade_No, P_Result_Code, "
			+ "P_Return_Code, P_Sign, P_Time_End, P_Totle_Fee, P_Coupon_Fee, "
			+ "P_Coupon_Count, P_Coupon_Type, P_Coupon_Id, P_Trade_Type, P_Transaction_Id) "
			+ "VALUES (#{pay.P_MID}, #{pay.P_OID}, #{pay.P_UID}, #{pay.P_Appid}, "
			+ "#{pay.P_Attach}, #{pay.P_Bank_Type}, #{pay.P_Fee_Type}, #{pay.P_Is_Subscribe}, "
			+ "#{pay.P_Mch_Id}, #{pay.P_Nonce_Str}, #{pay.P_Openid}, #{pay.P_Out_Trade_No}, #{pay.P_Result_Code}, "
			+ "#{pay.P_Return_Code}, #{pay.P_Sign}, #{pay.P_Time_End}, #{pay.P_Totle_Fee}, #{pay.P_Coupon_Fee}, "
			+ "#{pay.P_Coupon_Count}, #{pay.P_Coupon_Type}, #{pay.P_Coupon_Id}, #{pay.P_Trade_Type}, #{pay.P_Transaction_Id})")
	void insert(@Param("pay") Pay pay);
	
	@Select("SELECT * FROM pay WHERE P_Out_Trade_No = #{o_outTrade_no}")
	Pay getByO_OutTrade_No(@Param("o_outTrade_no") String o_outTrade_no);
	
	@Select("SELECT * FROM pay WHERE P_OID = #{o_id}")
	Pay getByO_ID(@Param("o_id") int o_id);
	
}
