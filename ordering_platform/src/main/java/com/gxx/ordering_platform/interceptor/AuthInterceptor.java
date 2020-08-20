package com.gxx.ordering_platform.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gxx.ordering_platform.utils.JWTUtils;

import io.jsonwebtoken.Claims;

@Component
public class AuthInterceptor implements HandlerInterceptor {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String jwtString = request.getHeader("Authorization");
		try {
			if (jwtString == null) {
				response.setStatus(401);
			} else {
				Claims claims = JWTUtils.parseJwt(jwtString);
				// 日志记录请求者信息
				logger.info("jwt拦截器，放行claims: " + claims);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	} 
}
