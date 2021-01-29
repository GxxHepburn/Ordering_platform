package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

import com.gxx.ordering_platform.entity.OrderReturnDetail;

public interface OrderReturnDetailMapper {

	@Insert("INSERT INTO orderreturndetail (ORD_ORID, ORD_OID, ORD_FID, ORD_FoodState, ORD_RealPrice, ORD_Spec, "
			+ "ORD_PropOne, ORD_PropTwo, ORD_Num, ORD_FName) VALUES (#{orderReturnDetail.ORD_ORID}, #{orderReturnDetail.ORD_OID}, "
			+ "#{orderReturnDetail.ORD_FID}, #{orderReturnDetail.ORD_FoodState}, #{orderReturnDetail.ORD_RealPrice}, "
			+ "#{orderReturnDetail.ORD_Spec}, #{orderReturnDetail.ORD_PropOne}, #{orderReturnDetail.ORD_PropTwo}, "
			+ "#{orderReturnDetail.ORD_Num}, #{orderReturnDetail.ORD_FName})")
	void insert(@Param("orderReturnDetail") OrderReturnDetail orderReturnDetail);
}
