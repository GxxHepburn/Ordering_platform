package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Mmngct;

public interface MmaMapper {

	@Select("SELECT * FROM mmngct WHERE MMA_UserName = #{mma_username}")
	Mmngct getByUsername(@Param("mma_username") String mma_username);
}
