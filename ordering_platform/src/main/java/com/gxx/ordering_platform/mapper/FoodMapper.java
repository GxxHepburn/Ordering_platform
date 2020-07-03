package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Food;

public interface FoodMapper {

	@Select("SELECT * FROM food WHERE F_FTID = #{f_ftid} AND F_MID = #{f_mid}")
	List<Food> getByMIDANDFTID(@Param("f_ftid") int f_ftid,@Param("f_mid") int f_mid);
}
