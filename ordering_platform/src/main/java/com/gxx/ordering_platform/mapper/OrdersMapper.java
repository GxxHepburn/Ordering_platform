package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
}
