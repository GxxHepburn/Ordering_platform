package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.OrderReturn;
import com.gxx.ordering_platform.entity.OrderReturnDetail;

public interface OrderReturnDetailMapper {

	@Insert("INSERT INTO orderreturndetail (ORD_ORID, ORD_OID, ORD_FID, ORD_FoodState, ORD_RealPrice, ORD_Spec, "
			+ "ORD_PropOne, ORD_PropTwo, ORD_Num, ORD_FName) VALUES (#{orderReturnDetail.ORD_ORID}, #{orderReturnDetail.ORD_OID}, "
			+ "#{orderReturnDetail.ORD_FID}, #{orderReturnDetail.ORD_FoodState}, #{orderReturnDetail.ORD_RealPrice}, "
			+ "#{orderReturnDetail.ORD_Spec}, #{orderReturnDetail.ORD_PropOne}, #{orderReturnDetail.ORD_PropTwo}, "
			+ "#{orderReturnDetail.ORD_Num}, #{orderReturnDetail.ORD_FName})")
	void insert(@Param("orderReturnDetail") OrderReturnDetail orderReturnDetail);
	
	@Select("SELECT * FROM orderreturndetail WHERE ORD_ORID = #{or_id}")
	List<OrderReturnDetail> getByOR_ID(@Param("or_id") int or_id);
}
