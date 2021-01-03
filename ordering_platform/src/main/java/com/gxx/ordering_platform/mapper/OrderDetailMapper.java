package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.OrderDetail;

public interface OrderDetailMapper {

	@Insert("INSERT INTO orderdetail (OD_OID, OD_FID, OD_FoodState, OD_RealPrice, OD_Spec, "
			+ "OD_PropOne, OD_PropTwo, OD_Num, OD_RealNum, OD_FName) VALUES (#{orderDetail.OD_OID}, "
			+ "#{orderDetail.OD_FID}, #{orderDetail.OD_FoodState}, #{orderDetail.OD_RealPrice}, "
			+ "#{orderDetail.OD_Spec}, #{orderDetail.OD_PropOne}, #{orderDetail.OD_PropTwo}, "
			+ "#{orderDetail.OD_Num}, #{orderDetail.OD_RealNum}, #{orderDetail.OD_FName})") 
	@Options(useGeneratedKeys = true, keyProperty = "OD_ID")
	int insert(@Param("orderDetail") OrderDetail orderDetail);
	
	@Select("SELECT * FROM orderdetail WHERE OD_OID = #{o_id}")
	List<OrderDetail> getByOrderId(@Param("o_id")int o_id);
}
