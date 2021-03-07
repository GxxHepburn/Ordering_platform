package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.QrCode;

public interface QrCodeMapper {
	
	@Select("SELECT * FROM qrcode WHERE Q_url = #{q_url}")
	QrCode getByUrl(@Param("q_url") String q_url);
	
	@Insert("INSERT INTO qrcode (Q_MID, Q_url, Q_TID, Q_TTID) VALUES (#{m_id}, #{q_url}, #{q_tid}, #{q_ttid})")
	void insert(@Param("m_id") int m_id, @Param("q_url") String q_url, @Param("q_tid") int q_tid, @Param("q_ttid") int q_ttid);
	
	@Delete("DELETE FROM qrcode WHERE Q_MID = #{m_id} AND Q_TID = #{q_tid}")
	void delete(@Param("m_id") int m_id, @Param("q_tid") int q_tid);
	
	@Delete("DELETE FROM qrcode WHERE Q_MID = #{m_id} AND Q_TTID = #{q_ttid}")
	void deleteByMIDTTID(@Param("m_id") int m_id, @Param("q_ttid") int q_ttid);
}
