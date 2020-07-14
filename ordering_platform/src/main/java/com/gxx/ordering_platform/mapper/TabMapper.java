package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Tab;

public interface TabMapper {

	@Select("SELECT * FROM tab WHERE T_ID = #{t_id}")
	Tab getByTabId(@Param("t_id") int t_id);
}
