package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.FoodProperty;

public interface FoodPropertyMapper {

	@Select("SELECT * FROM foodproperty WHERE FP_MID = #{fp_mid} AND "
			+ "FP_FTID = #{fp_ftid} AND FP_FID = #{fp_fid}")
	List<FoodProperty> getByMIDAndFTIDAndFID(@Param("fp_mid") int fp_mid,
			@Param("fp_ftid") int fp_ftid, @Param("fp_fid") int fp_fid);
}
