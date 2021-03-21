package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.OrderDetail;

public interface OrderDetailMapper {

	@Insert("INSERT INTO orderdetail (OD_OID, OD_OAID, OD_FID, OD_FoodState, OD_RealPrice, OD_Spec, "
			+ "OD_PropOne, OD_PropTwo, OD_Num, OD_RealNum, OD_FName) VALUES (#{orderDetail.OD_OID}, "
			+ "#{orderDetail.OD_OAID}, #{orderDetail.OD_FID}, #{orderDetail.OD_FoodState}, #{orderDetail.OD_RealPrice}, "
			+ "#{orderDetail.OD_Spec}, #{orderDetail.OD_PropOne}, #{orderDetail.OD_PropTwo}, "
			+ "#{orderDetail.OD_Num}, #{orderDetail.OD_RealNum}, #{orderDetail.OD_FName})") 
	@Options(useGeneratedKeys = true, keyProperty = "OD_ID")
	int insert(@Param("orderDetail") OrderDetail orderDetail);
	
	@Select("SELECT * FROM orderdetail WHERE OD_OID = #{o_id}")
	List<OrderDetail> getByOrderId(@Param("o_id")int o_id);
	
	@Select("SELECT * FROM orderdetail WHERE OD_OAID = #{oa_id}")
	List<OrderDetail> getByOA_ID(@Param("oa_id")int oa_id);
	
	@Delete("DELETE FROM orderdetail WHERE OD_ID = #{od_id}")
	void deleteByOD_ID(@Param("od_id") int od_id);
	
	@Update("UPDATE orderdetail set OD_Num = #{od_num}, OD_RealNum = #{od_realNum} WHERE OD_ID = #{od_id}")
	void updateRealNumAndNumByOD_ID(@Param("od_id") int od_id, @Param("od_num") int od_num, @Param("od_realNum") int od_realNum);
	
	@Delete("DELETE FROM orderdetail WHERE OD_OID = #{oid}")
	void deleteByOID(@Param("oid") int oid);
}
