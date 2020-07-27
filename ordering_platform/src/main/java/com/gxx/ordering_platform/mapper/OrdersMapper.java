package com.gxx.ordering_platform.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Orders;

public interface OrdersMapper {

	@Insert("INSERT INTO orders (O_MID, O_UID, O_TID, O_TotlePrice, O_PayStatue, O_OrderingTime,"
			+ " O_Remarks, O_TotleNum, O_UniqSearchID) VALUES (#{orders.O_MID}, #{orders.O_UID}, #{orders.O_TID}, "
			+ "#{orders.O_TotlePrice}, #{orders.O_PayStatue}"
			+ ", #{orders.O_OrderingTime}, #{orders.O_Remarks}, #{orders.O_TotleNum}, #{orders.O_UniqSearchID})")
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
	
	@Select("SELECT * FROM orders WHERE O_UID = #{o_uid} AND O_PayStatue = 0 OR O_PayStatue = 3 ORDER BY O_OrderingTime DESC")
	List<Orders> getOrdersOrderByTimeNow(@Param("o_uid") int o_uid);
	
	@Select("SELECT * FROM orders WHERE O_UID = #{o_uid} AND O_PayStatue = 1 ORDER BY O_OrderingTime DESC")
	List<Orders> getOrdersOrderByTimeFinished(@Param("o_uid") int o_uid);
	
	@Select("SELECT * FROM orders WHERE O_UID = #{o_uid} AND O_PayStatue = 2 ORDER BY O_OrderingTime DESC")
	List<Orders> getOrdersOrderByTimeReturn(@Param("o_uid") int o_uid);
}
