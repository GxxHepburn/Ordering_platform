package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.OrderAdd;

public interface OrderAddMapper {

	@Insert("INSERT INTO orderadd (OA_OID, OA_Sort, OA_MID, OA_UID, OA_TID, OA_OrderingTime, "
			+ "OA_TotleNum) VALUES (#{orderAdd.OA_OID}, #{orderAdd.OA_Sort}, #{orderAdd.OA_MID}, "
			+ "#{orderAdd.OA_UID}, #{orderAdd.OA_TID}, #{orderAdd.OA_OrderingTime}, #{orderAdd.OA_TotleNum})")
	@Options(useGeneratedKeys = true, keyProperty = "OA_ID")
	int insert(@Param("orderAdd") OrderAdd orderAdd);
	
	@Update("UPDATE orderadd SET OA_TotlePrice = #{oa_totlePrice} WHERE OA_ID = #{oa_id}")
	void updateTotlePrice(@Param("oa_id") int oa_id, @Param("oa_totlePrice") float oa_totlePrice);
	
	@Select("SELECT COUNT(*) FROM orderadd WHERE OA_OID = #{oa_oid}")
	int selectCountByOA_OID(@Param("oa_oid") int oa_oid);
	
	@Select("SELECT * FROM orderadd WHERE OA_OID = #{oa_oid} ORDER BY OA_Sort")
	List<OrderAdd> getByO_ID(@Param("oa_oid") int oa_oid);
}
