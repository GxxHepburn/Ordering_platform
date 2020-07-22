package com.gxx.ordering_platform.wxPaySDK;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
public class MyWXPayConfig extends WXPayConfig {

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Value("${wechat.appId}")
	String appId;
	
	@Value("${merchantNumber.mchId}")
	String mchId;
	
	@Value("${merchantNumber.mchKey}")
	String mchKey;
	
	@Override
	String getAppID() {
		// TODO Auto-generated method stub
		return this.appId;
	}

	@Override
	String getMchID() {
		// TODO Auto-generated method stub
		return this.mchId;
	}

	@Override
	String getKey() {
		// TODO Auto-generated method stub
		return this.mchKey;
	}

	@Override
	InputStream getCertStream() {
		// TODO Auto-generated method stub
		return null;
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
