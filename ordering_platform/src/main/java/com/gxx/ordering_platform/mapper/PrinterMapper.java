package com.gxx.ordering_platform.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.Printer;

public interface PrinterMapper {
	
	@Select("SELECT * FROM printer WHERE P_MID = #{m_id}")
	List<Printer> getByMID(@Param("m_id") int m_id);
}
