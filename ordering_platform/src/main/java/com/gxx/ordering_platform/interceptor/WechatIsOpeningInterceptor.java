package com.gxx.ordering_platform.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.mapper.MerMapper;

@Order(2)
@Component
public class WechatIsOpeningInterceptor implements HandlerInterceptor {

	@Autowired MerMapper merMapper;
	
	final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		Integer m_id = Integer.valueOf(request.getHeader("mid"));
		// 如果没营业,response.setStatus(403)，return false
		// 如果营业了，return true
		Mer mer = merMapper.getMerByMID(m_id);
		if (mer.getM_IsInOpenTime() == 0) {
			
			response.setStatus(403);
			return false;
		}
		return true;
	}
}
