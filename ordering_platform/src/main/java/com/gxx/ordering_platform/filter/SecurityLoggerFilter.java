package com.gxx.ordering_platform.filter;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;


@Component
public class SecurityLoggerFilter implements Filter {
	
	private final Logger logger = LoggerFactory.getLogger("securityLog");

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		//ip
		String ip = req.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = req.getRemoteAddr();
		}
		if (ip.indexOf(",") != -1) {
			String[] ips = ip.split(",");
			ip = ips[0].trim();
		}
		//url
		String url = req.getRequestURL().toString();
		
		UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("User-Agent"));
		//浏览器版本
		Browser browser = userAgent.getBrowser();
		String browserVersion = browser.toString();
		
		//系统版本信息
		OperatingSystem os = userAgent.getOperatingSystem();
		String systemInfo = os.toString();
		
		//来访者主机名称
		InetAddress inet = null;
		try {
			inet = InetAddress.getByName(ip);
		} catch (UnknownHostException  e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		logger.info("start new visit---------------------");
		logger.info("ip: " + ip);
		logger.info("url: " + url);
		logger.info("browserVersion: " + browserVersion);
		logger.info("systemInfo: " + systemInfo);
		logger.info("hostName: " + inet.getHostName());
		logger.info("port: " + req.getRemotePort());
		logger.info(" ");
		
		chain.doFilter(request, response);
	}
}
