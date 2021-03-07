package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.QrCode;

public interface QrCodeMapper {
	
	@Select("SELECT * FROM qrcode WHERE Q_url = #{q_url}")
	QrCode getByUrl(@Param("q_url") String q_url);
	
	@Insert("INSERT INTO qrcode (Q_MID, Q_url) VALUES (#{m_id}, #{q_url})")
	void insert(@Param("m_id") int m_id, @Param("q_url") String q_url);
}
