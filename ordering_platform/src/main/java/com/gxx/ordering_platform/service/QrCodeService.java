package com.gxx.ordering_platform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gxx.ordering_platform.entity.QrCode;
import com.gxx.ordering_platform.mapper.QrCodeMapper;

@Component
public class QrCodeService {

	@Autowired
	QrCodeMapper qrCodeMapper;
	
	@Transactional
	public String checking(String url) {
		QrCode qrCode = qrCodeMapper.getByUrl(url);
		
		if (qrCode == null) {
			return "0";
		} else {
			return "1";
		}
	}
}
