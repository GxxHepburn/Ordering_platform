package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.FoodType;

public interface FoodTypeMapper {

	@Select("SELECT * FROM foodtype WHERE FT_MID = #{ft_mid}")
	List<FoodType> getByFTMID(@Param("ft_mid") int ft_mid);
}
