package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.OrderReturn;

public interface OrderReturnMapper {

	@Select("SELECT COUNT(*) FROM orderreturn WHERE OR_OID = #{or_oid}")
	int selectCountByOR_OID(@Param("or_oid") int or_oid);
	
	@Insert("INSERT INTO orderreturn (OR_OID, OR_Sort, OR_MID, OR_UID, OR_TID, OR_ReturnTime) VALUES ("
			+ "#{orderReturn.OR_OID}, #{orderReturn.OR_Sort}, #{orderReturn.OR_MID}, #{orderReturn.OR_UID}, "
			+ "#{orderReturn.OR_TID}, #{orderReturn.OR_ReturnTime})")
	@Options(useGeneratedKeys = true, keyProperty = "OR_ID")
	void insert(@Param("orderReturn") OrderReturn orderReturn);
	
	@Update("UPDATE orderreturn SET OR_TotlePrice = #{or_totlePrice} WHERE OR_ID = #{or_id}")
	void updateTotlePrice(@Param("or_id") int or_id, @Param("or_totlePrice") float or_totlePrice);
}
