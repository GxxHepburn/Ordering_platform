package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.Orders;

public interface OrdersMapper {

	@Insert("INSERT INTO orders (O_MID, O_UID, O_TID, O_TotlePrice, O_PayStatue, O_OrderingTime,"
			+ " O_Remarks, O_TotleNum, O_UniqSearchID, O_NumberOfDiners, O_IsPayNow) VALUES (#{orders.O_MID}, #{orders.O_UID}, #{orders.O_TID}, "
			+ "#{orders.O_TotlePrice}, #{orders.O_PayStatue}"
			+ ", #{orders.O_OrderingTime}, #{orders.O_Remarks}, #{orders.O_TotleNum}, #{orders.O_UniqSearchID}, #{orders.O_NumberOfDiners}"
			+ ", #{orders.O_IsPayNow})")
	@Options(useGeneratedKeys = true, keyProperty = "O_ID")
	int insert(@Param("orders") Orders orders);
	
	@Select("SELECT * FROM orders WHERE O_UniqSearchID = #{o_uniqsearchid}")
	Orders selectBySearchId(@Param("o_uniqsearchid") String o_uniqsearchid);
	
	@Update("UPDATE orders SET O_isPayNow = #{o_ispaynow}, "
			+ "O_OutTradeNo = #{o_outtradeno} WHERE O_UniqSearchID = #{o_uniqsearchid}")
	boolean updateOut_Trade_NoBySearchId(@Param("o_ispaynow") int o_ispaynow, 
			@Param("o_outtradeno") String o_outtradeno, @Param("o_uniqsearchid") String o_uniqsearchid);
	
	@Update("UPDATE orders SET O_TotleNum = #{o_totlenum}, O_TotlePrice = #{o_totleprice} WHERE O_UniqSearchID = #{o_uniqsearchid}")
	boolean updateNumAndPriceBySearchId(@Param("o_uniqsearchid")String orderSearchId, @Param("o_totlenum") int totalNum, @Param("o_totleprice") float totalPrice);
	
	@Update("UPDATE orders SET O_isPayNow = #{o_ispaynow}, "
			+ "O_PayTime = #{o_paytime}, O_PayStatue = #{o_paystatue} WHERE O_OutTradeNo = #{o_outtradeno}")
	boolean updatePaied(@Param("o_outtradeno")String o_outtradeno,@Param("o_ispaynow") int isPayNow, 
			@Param("o_paystatue") int payStatues, @Param("o_paytime") Date payTime);
	
	@Update("UPDATE orders SET O_isPayNow = #{o_ispaynow} WHERE O_UniqSearchID = #{o_uniqsearchid}")
	boolean updateIsPay(@Param("o_uniqsearchid") String o_uniqsearchid, @Param("o_ispaynow") int isPay);
	
	@Select("SELECT * FROM orders WHERE O_UID = #{o_uid} AND (O_PayStatue = 0 OR O_PayStatue = 3) ORDER BY O_OrderingTime DESC limit #{limitStart}, #{limitSize}")
	List<Orders> getOrdersOrderByTimeNow(@Param("o_uid") int o_uid, @Param("limitStart") int limitStart, @Param("limitSize") int limitSize);
	
	@Select("SELECT * FROM orders WHERE O_UID = #{o_uid} AND O_PayStatue = 1 ORDER BY O_OrderingTime DESC limit #{limitStart}, #{limitSize}")
	List<Orders> getOrdersOrderByTimeFinished(@Param("o_uid") int o_uid, @Param("limitStart") int limitStart, @Param("limitSize") int limitSize);
	
	@Select("SELECT * FROM orders WHERE O_UID = #{o_uid} AND O_PayStatue = 2 ORDER BY O_OrderingTime DESC limit #{limitStart}, #{limitSize}")
	List<Orders> getOrdersOrderByTimeReturn(@Param("o_uid") int o_uid, @Param("limitStart") int limitStart, @Param("limitSize") int limitSize);

	
	@Select("SELECT O_ID, O_MID, O_UID, O_TID, O_TotlePrice, O_PayMethod, O_PayStatue, O_OrderingTime, O_PayTime, "
			+ "O_OutTradeNo, O_Remarks, O_TotleNum, O_UniqSearchID, O_isPayNow, O_ReturnNum, O_NumberOfDiners, T_Name, TT_Name FROM orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID WHERE O_UniqSearchID = #{o_uniqSearchID} ORDER BY O_OrderingTime DESC")
	List<Multi_Orders_Tab_Tabtype> getOrdersByUniqSearchIDOrderByIimeDESC(@Param("o_uniqSearchID") String o_uniqSearchID);
	
	@Select("<script>"
			+ "SELECT O_ID, O_MID, O_UID, O_TID, O_TotlePrice, O_PayMethod, O_PayStatue, O_OrderingTime, O_PayTime, "
			+ "O_OutTradeNo, O_Remarks, O_TotleNum, O_UniqSearchID, O_isPayNow, O_ReturnNum, O_NumberOfDiners, T_Name, TT_Name "
			+ "FROM orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE 1=1"
			+ "<if test='o_uid!=null'>"
			+ " AND O_UID = #{o_uid}"
			+ "</if>"
			+ "<if test='o_tid!=null'>"
			+ " AND O_TID = #{o_tid}"
			+ "</if>"
			+ "<if test='tt_id!=null'>"
			+ " AND tabtype.TT_ID = #{tt_id}"
			+ "</if>"
			+ "<if test='orderStartTime!=null'>"
			+ " AND O_OrderingTime &gt;= #{orderStartTime}"
			+ "</if>"
			+ "<if test='orderEndTime!=null'>"
			+ " AND O_OrderingTime &lt;= #{orderEndTime}"
			+ "</if>"
			+ "<if test='payStartTime!=null'>"
			+ " AND O_PayTime &gt;= #{payStartTime}"
			+ "</if>"
			+ "<if test='payEndTime!=null'>"
			+ " AND O_PayTime &lt;= #{payEndTime}"
			+ "</if>"
			+ "<if test='payStatus!=null'>"
			+ " AND O_PayStatue = #{payStatus}"
			+ "</if>"
			+ " AND O_MID = #{m_id}"
			+ " ORDER BY O_OrderingTime DESC"
			+ " limit #{limitStart}, #{pagesizeInt}"
			+ "</script>")
	List<Multi_Orders_Tab_Tabtype> getOrdersByUIDTabIDTabtypeIDOorderTimePayTimeOrderByIimeDESC(
			@Param("o_uid") Integer o_uid, @Param("o_tid") Integer o_tid, @Param("tt_id") Integer tt_tid,
			@Param("orderStartTime") Date orderStartTime, @Param("orderEndTime") Date orderEndTime,
			@Param("payStartTime") Date payStartTime, @Param("payEndTime") Date payEndTime,
			@Param("m_id") int m_id,
			@Param("limitStart") int limitStart, @Param("pagesizeInt") int pagesizeInt,
			@Param("payStatus") Integer PayStatus);
	
	@Select("<script>"
			+ "SELECT COUNT(*) "
			+ "FROM orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE 1=1"
			+ "<if test='o_uid!=null'>"
			+ " AND O_UID = #{o_uid}"
			+ "</if>"
			+ "<if test='o_tid!=null'>"
			+ " AND O_TID = #{o_tid}"
			+ "</if>"
			+ "<if test='tt_id!=null'>"
			+ " AND tabtype.TT_ID = #{tt_id}"
			+ "</if>"
			+ "<if test='orderStartTime!=null'>"
			+ " AND O_OrderingTime &gt;= #{orderStartTime}"
			+ "</if>"
			+ "<if test='orderEndTime!=null'>"
			+ " AND O_OrderingTime &lt;= #{orderEndTime}"
			+ "</if>"
			+ "<if test='payStartTime!=null'>"
			+ " AND O_PayTime &gt;= #{payStartTime}"
			+ "</if>"
			+ "<if test='payEndTime!=null'>"
			+ " AND O_PayTime &lt;= #{payEndTime}"
			+ "</if>"
			+ "<if test='payStatus!=null'>"
			+ " AND O_PayStatue = #{payStatus}"
			+ "</if>"
			+ " AND O_MID = #{m_id}"
			+ "</script>")
	int getOrdersTotalByUIDTabIDTabtypeIDOorderTimePayTime(
			@Param("o_uid") Integer o_uid, @Param("o_tid") Integer o_tid, @Param("tt_id") Integer tt_tid,
			@Param("orderStartTime") Date orderStartTime, @Param("orderEndTime") Date orderEndTime,
			@Param("payStartTime") Date payStartTime, @Param("payEndTime") Date payEndTime,
			@Param("m_id") int m_id,
			@Param("payStatus") Integer PayStatus);
	
	@Update("UPDATE orders SET O_TotlePrice = #{o_totlePrice} WHERE O_ID = #{o_id}")
	void updateTotlePrice(@Param("o_id") int o_id, @Param("o_totlePrice") float o_totlePrice);
	
	@Select("SELECT O_ID, O_MID, O_UID, O_TID, O_TotlePrice, O_PayMethod, O_PayStatue, O_OrderingTime, O_PayTime, "
			+ "O_OutTradeNo, O_Remarks, O_TotleNum, O_UniqSearchID, O_isPayNow, O_ReturnNum, O_NumberOfDiners, T_Name, TT_Name FROM "
			+ "orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE O_ID = #{o_id}")
	Multi_Orders_Tab_Tabtype getOrderForm(@Param("o_id") int o_id);
	
	@Update("UPDATE orders SET O_PayStatue = #{o_payStatue} WHERE O_ID = #{o_id}")
	void updateO_PayStatueByO_ID(@Param("o_id") int o_id, @Param("o_payStatue") int o_payStatue);
	
	@Select("SELECT * FROM orders WHERE O_OutTradeNo = #{o_outTradeNo}")
	Orders getOrderByO_OutTradeNo(@Param("o_outTradeNo") String o_outTradeNo);
	
	@Select("SELECT O_ID, O_MID, O_UID, O_TID, O_TotlePrice, O_PayMethod, O_PayStatue, O_OrderingTime, O_PayTime, "
			+ "O_OutTradeNo, O_Remarks, O_TotleNum, O_UniqSearchID, O_isPayNow, O_ReturnNum, O_NumberOfDiners, T_Name, "
			+ "TT_Name FROM orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE O_OutTradeNo = #{p_out_trade_no}")
	Multi_Orders_Tab_Tabtype getOrderWithTNameAndTTNameByO_OutTradeNo(@Param("p_out_trade_no") String p_out_trade_no);
	
	@Select("SELECT * FROM orders WHERE O_ID = #{o_id}")
	Orders getordersByO_ID(@Param("o_id") int o_id);
	
	@Select("SELECT * FROM orders WHERE O_UniqSearchID = #{searchId}")
	Orders getOrdersByUniqSearchID(@Param("searchId") String searchId);
	
	
	@Select("SELECT O_ID, O_MID, O_UID, O_TID, O_TotlePrice, O_PayMethod, O_PayStatue, O_OrderingTime, O_PayTime, "
			+ "O_OutTradeNo, O_Remarks, O_TotleNum, O_UniqSearchID, O_isPayNow, O_ReturnNum, O_NumberOfDiners, T_Name, TT_Name FROM "
			+ "orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE O_MID = #{m_id} "
			+ "AND O_OrderingTime >= #{lastDate} "
			+ "ORDER BY O_OrderingTime DESC "
			+ "limit #{limitStart}, #{pagesizeInt}")
	List<Multi_Orders_Tab_Tabtype> getLastOrdersByMIDDESC(@Param("m_id") int m_id, @Param("lastDate") Date lastDate,
			@Param("limitStart") int limitStart, @Param("pagesizeInt") int pagesizeInt);
	
	@Select("SELECT COUNT(*) FROM orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE O_MID = #{m_id} AND O_OrderingTime >= #{lastDate}")
	int getLastOrdersTotal(@Param("m_id") int m_id, @Param("lastDate") Date lastDate);
	
	@Select("SELECT O_ID, O_MID, O_UID, O_TID, O_TotlePrice, O_PayMethod, O_PayStatue, O_OrderingTime, O_PayTime, "
			+ "O_OutTradeNo, O_Remarks, O_TotleNum, O_UniqSearchID, O_isPayNow, O_ReturnNum, O_NumberOfDiners, T_Name, TT_Name FROM "
			+ "orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE O_MID = #{m_id} "
			+ "AND O_PayStatue = #{payStatus} "
			+ "ORDER BY O_OrderingTime DESC "
			+ "limit #{limitStart}, #{pagesizeInt}")
	List<Multi_Orders_Tab_Tabtype> getOrdersByMIDANDPayStatusDESC(@Param("m_id") int m_id, @Param("payStatus") int payStatus,
			@Param("limitStart") int limitStart, @Param("pagesizeInt") int pagesizeInt);
	
	@Select("SELECT COUNT(*) FROM orders left join tab on tab.T_ID = orders.O_TID left join tabtype on tabtype.TT_ID = tab.T_TTID "
			+ "WHERE O_MID = #{m_id} AND O_PayStatue = #{payStatus}")
	int getOrdersTotalByMIDANDPayStatus(@Param("m_id") int m_id, @Param("payStatus") int payStatus);
}
