package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Multi_Pay_Orders_Tab_TabType;
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
	
	@Select("SELECT * FROM pay WHERE P_Transaction_Id = #{p_transaction_id}")
	Pay getByTransactionId(@Param("p_transaction_id") String p_transaction_id);
	
	@Select("SELECT * FROM pay WHERE P_OID = #{o_id}")
	Pay getByO_ID(@Param("o_id") int o_id);
	
	@Select("<script>"
			+ "SELECT * FROM pay left join orders on orders.O_ID = pay.P_OID left join tab on tab.T_ID = orders.O_TID "
			+ "left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE P_MID = #{m_id} "
			+ "<if test='u_id!=null'>"
			+ " AND P_UID = #{u_id}"
			+ "</if>"
			+ "<if test='o_id!=null'>"
			+ " AND P_OID = #{o_id}"
			+ "</if>"
			+ "<if test='outTradeNo!=null'>"
			+ " AND P_Out_Trade_No = #{outTradeNo}"
			+ "</if>"
			+ "<if test='transactionId!=null'>"
			+ " AND P_Transaction_Id = #{transactionId}"
			+ "</if>"
			+ "<if test='tabId!=null'>"
			+ " AND orders.O_TID = #{tabId}"
			+ "</if>"
			+ "<if test='tabTypeId!=null'>"
			+ " AND tabtype.TT_ID = #{tabTypeId}"
			+ "</if>"
			+ "<if test='payStartTime!=null'>"
			+ " AND P_Time_End &gt;= #{payStartTime}"
			+ "</if>"
			+ "<if test='payEndTime!=null'>"
			+ " AND P_Time_End &lt;= #{payEndTime}"
			+ "</if>"
			+ " ORDER BY P_Time_End DESC"
			+ " limit #{limitStart}, #{pagesizeInt}"
			+ "</script>")
	List<Multi_Pay_Orders_Tab_TabType> getByUID_UniqSearchID_OutTradeNo_TransactionId_PayTime_TabId_TabTypeId(@Param("m_id") Integer m_id, 
			@Param("u_id") Integer u_id, @Param("o_id") Integer o_id, @Param("outTradeNo") String outTradeNo, 
			@Param("transactionId") String transactionId, @Param("payStartTime") String payStartTime, 
			@Param("payEndTime") String payEndTime, @Param("tabTypeId") Integer tabTypeId, @Param("tabId") Integer tabId, 
			@Param("limitStart") Integer limitStart, @Param("pagesizeInt") Integer pagesizeInt);
	
	@Select("<script>"
			+ "SELECT COUNT(*) FROM pay left join orders on orders.O_ID = pay.P_OID left join tab on tab.T_ID = orders.O_TID "
			+ "left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE P_MID = #{m_id} "
			+ "<if test='u_id!=null'>"
			+ " AND P_UID = #{u_id}"
			+ "</if>"
			+ "<if test='o_id!=null'>"
			+ " AND P_OID = #{o_id}"
			+ "</if>"
			+ "<if test='outTradeNo!=null'>"
			+ " AND P_Out_Trade_No = #{outTradeNo}"
			+ "</if>"
			+ "<if test='transactionId!=null'>"
			+ " AND P_Transaction_Id = #{transactionId}"
			+ "</if>"
			+ "<if test='tabId!=null'>"
			+ " AND orders.O_TID = #{tabId}"
			+ "</if>"
			+ "<if test='tabTypeId!=null'>"
			+ " AND tabtype.TT_ID = #{tabTypeId}"
			+ "</if>"
			+ "<if test='payStartTime!=null'>"
			+ " AND P_Time_End &gt;= #{payStartTime}"
			+ "</if>"
			+ "<if test='payEndTime!=null'>"
			+ " AND P_Time_End &lt;= #{payEndTime}"
			+ "</if>"
			+ "</script>")
	int getPayTotalByUID_UniqSearchID_OutTradeNo_TransactionId_PayTime_TabId_TabTypeId(@Param("m_id") Integer m_id, 
			@Param("u_id") Integer u_id, @Param("o_id") Integer o_id, @Param("outTradeNo") String outTradeNo, 
			@Param("transactionId") String transactionId, @Param("payStartTime") String payStartTime, 
			@Param("payEndTime") String payEndTime, @Param("tabTypeId") Integer tabTypeId, @Param("tabId") Integer tabId);
}
