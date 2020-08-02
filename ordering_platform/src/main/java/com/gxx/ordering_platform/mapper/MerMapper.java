package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Mer;

public interface MerMapper {
	
	@Select("SELECT * FROM mer WHERE M_ID = #{m_id}")
	Mer getMerByMID(@Param("m_id") int m_id);
}
