package com.gxx.ordering_platform.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.gxx.ordering_platform.entity.QrCode;

public interface QrCodeMapper {
	
	@Select("SELECT * FROM qrcode WHERE Q_url = q_url")
	QrCode getByUrl(@Param("q_url") String url);
}
