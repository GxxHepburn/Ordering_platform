package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.gxx.ordering_platform.entity.Mer;

public interface MerMapper {
	
	@Select("SELECT * FROM mer WHERE M_ID = #{m_id}")
	Mer getMerByMID(@Param("m_id") int m_id);
	
	@Update("UPDATE mer SET M_Name = #{m_name}, M_Address = #{m_address}, M_Phone = #{m_phone}, M_BeginTime = #{m_beginTime}, "
			+ "M_EndTime = #{m_endTime}, M_img = #{m_img} WHERE M_ID = #{m_id}")
	void updateMerInfo(@Param("m_id") int m_id, @Param("m_name") String m_name,
			 @Param("m_address") String m_address, @Param("m_phone") String m_phone,
			 @Param("m_beginTime") String m_beginTime, @Param("m_endTime") String m_endTime,
			 @Param("m_img") String m_img);
}
