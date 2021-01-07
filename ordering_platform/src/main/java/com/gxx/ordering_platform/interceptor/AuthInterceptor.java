package com.gxx.ordering_platform.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gxx.ordering_platform.utils.JWTUtils;

import io.jsonwebtoken.Claims;

@Order(1)
@Component
public class AuthInterceptor implements HandlerInterceptor {
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String jwtString = request.getHeader("Authorization");
		Claims claims = null;
		try {
			if (jwtString == null) {
				response.setStatus(401);
			} else {
				claims = JWTUtils.parseJwt(jwtString);
				// 日志记录请求者信息
				logger.info("jwt拦截器，放行claims: " + claims);
				// 过期会抛出错误
				return true;
			}
		} catch (Exception e) {
			response.setStatus(400);
		}
		logger.info("jwt拦截器，拦截claims: " + claims);
		return false;
	} 
}
