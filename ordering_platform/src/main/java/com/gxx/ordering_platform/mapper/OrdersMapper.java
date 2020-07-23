package com.gxx.ordering_platform.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Orders;

public interface OrdersMapper {

	@Insert("INSERT INTO orders (O_MID, O_UID, O_TID, O_TotlePrice, O_PayStatue, O_OrderingTime,"
			+ " O_Remarks, O_TotleNum, O_UniqSearchID) VALUES (#{orders.O_ID}, #{orders.O_UID}, #{orders.O_TID}, "
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
	
	@Update("UPDATE orders SET O_isPayNow = #{o_ispaynow}, "
			+ "O_PayTime = #{o_paytime}, O_PayStatue = #{o_paystatue} WHERE O_OutTradeNo = #{o_outtradeno}")
	boolean updatePaied(@Param("o_outtradeno")String o_outtradeno,@Param("o_ispaynow") int isPayNow, 
			@Param("o_paystatue") int payStatues, @Param("o_paytime") Date payTime);
	@Update("UPDATE orders SET O_isPayNow = #{o_ispaynow} WHERE O_UniqSearchID = #{o_uniqsearchid}")
	boolean updateIsPay(@Param("o_uniqsearchid") String o_uniqsearchid, @Param("o_ispaynow") int isPay);
}
