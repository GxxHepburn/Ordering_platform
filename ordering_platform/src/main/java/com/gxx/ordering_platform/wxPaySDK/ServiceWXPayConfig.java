package com.gxx.ordering_platform.wxPaySDK;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.AppConfig;
import com.gxx.ordering_platform.wxPaySDK.IWXPayDomain.DomainInfo;

@Component
public class ServiceWXPayConfig extends WXPayConfig {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${serviceNumber.appid}")
	String service_appid;
	
	@Value("${serviceNumber.mch_id}")
	String service_mch_id;
	
	@Value("${serviceNumber.mchKey}")
	String service_mchKey;
	
	private byte[] certData;

	public ServiceWXPayConfig() throws Exception {
		try (InputStream inputStream = AppConfig.class.getClassLoader().getResourceAsStream("13028224_wxpay_apiclient_cert.p12");) {
			this.certData = new byte[10240];
			inputStream.read(this.certData);
		}
	}

	@Override
	String getAppID() {
		// TODO Auto-generated method stub
		return this.service_appid;
	}

	@Override
	String getMchID() {
		// TODO Auto-generated method stub
		return this.service_mch_id;
	}

	@Override
	String getKey() {
		// TODO Auto-generated method stub
		return this.service_mchKey;
	}

	@Override
	InputStream getCertStream() {
		
		ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
	}

	@Override
	IWXPayDomain getWXPayDomain() {
		// TODO Auto-generated method stub
		return new IWXPayDomain() {
			
			@Override
			public void report(String domain, long elapsedTimeMillis, Exception ex) {
				// TODO Auto-generated method stub
				logger.info("report");
			}
			
			@Override
			public DomainInfo getDomain(WXPayConfig config) {
				// TODO Auto-generated method stub
				return new IWXPayDomain.DomainInfo(WXPayConstants.DOMAIN_API, true);
			}
		};
	}
}
