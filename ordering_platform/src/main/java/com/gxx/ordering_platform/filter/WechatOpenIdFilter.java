package com.gxx.ordering_platform.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.request.WechatOpenIdFilterHttpServletRequest;
import com.gxx.ordering_platform.service.WechatLoginService;


@Component
public class WechatOpenIdFilter implements Filter {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	WechatLoginService WechatLoginService;

	//这个拦截器的目的是防止非法侵入，确保安全性
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest) request;
		InputStream inputStream = req.getInputStream();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for(;;) {
			int len = inputStream.read(buffer);
			if (len == -1) {
				break;
			}
			output.write(buffer, 0, len);
		}
		String jsonString = output.toString();
		logger.info(jsonString);
		JSONObject jsonObject = new JSONObject(jsonString);
		String openId = jsonObject.getString("openid");
		
		if ("".equals(openId)) {
			//拦截
			return;
		} else {
			WechatUser wechatUser = WechatLoginService.getUserByUOpenId(openId);
			if (wechatUser == null) {
				//拦截
				return;
			}
			//放行
			chain.doFilter(new WechatOpenIdFilterHttpServletRequest(req, output.toByteArray()), response);
		}
	}
}
