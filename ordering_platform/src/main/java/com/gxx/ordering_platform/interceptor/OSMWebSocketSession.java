package com.gxx.ordering_platform.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import com.gxx.ordering_platform.handler.OSMOrderingHandler;
import com.gxx.ordering_platform.utils.JWTUtils;

import io.jsonwebtoken.Claims;


@Component
@Order(2)
public class OSMWebSocketSession extends HttpSessionHandshakeInterceptor {

	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {
		// TODO Auto-generated method stub
		HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
		// 验证token
		Claims claims = null;
		String token = servletRequest.getParameter("token");
		if ("null".equals(token)) {
			servletResponse.setStatus(401);
			logger.info("jwt拦截器，拦截claims: " + claims);
			return false;
		}
		try {
			claims = JWTUtils.parseJwt(token);
			logger.info("jwt拦截器，放行claims: " + claims);
		} catch (Exception e) {
			// TODO: handle exception
			servletResponse.setStatus(400);
			logger.info("jwt拦截器，拦截claims: " + claims);
			return false;
		}
		
		attributes.put("name", servletRequest.getParameter("name"));
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}
}
