package com.gxx.ordering_platform.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.entity.Multi_Orders_Tab;
import com.gxx.ordering_platform.entity.Multi_WechatUser_Orders;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.WechatUser;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.service.MmaService;
import com.gxx.ordering_platform.service.OSMOrderDetailService;
import com.gxx.ordering_platform.service.OSMOrderingService;
import com.gxx.ordering_platform.service.OSMUsersService;
import com.gxx.ordering_platform.utils.JWTUtils;

@RestController
@RequestMapping("/OSM")
public class OSMController {
	
	@Value("classpath:/menu.json")
	private Resource resource;
	
	private String mensJsonString;
	
	@Autowired
	WechatUserMapper wechatUserMapper;
	
	@Autowired
	MmaMapper mmaMapper;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@PostConstruct
	public void init() throws IOException {
		try (var reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
			this.mensJsonString = reader.lines().collect(Collectors.joining("\n"));
		}
	}

	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	MmaService mmaService;
	
	@Autowired
	OSMOrderDetailService oSMOrderDetailService;
	
	@Autowired OSMUsersService oSMUsersService;
	
	@Autowired OSMOrderingService oSMOrderingService;
	
	@PostMapping("/login")
	@ResponseBody
	public String login(@RequestBody String str) {
		
		//获取请求参数-用户名，密码
		JSONObject requestJsonObject = new JSONObject(str);
		String username = requestJsonObject.getString("username");
		String password = requestJsonObject.getString("password");
		
		return mmaService.login(username, password);
	}
	
	@GetMapping("/menus")
	@ResponseBody
	public String menus() {
		return this.mensJsonString;
	}
	
	@GetMapping("/users")
	@ResponseBody
	public String users(String query, String pagenum, String pagesize, String mmngctUserName){
		
		return oSMUsersService.users(query, pagenum, pagesize, mmngctUserName);
	}
	
	@PostMapping("/userOrdersList")
	@ResponseBody
	public String userOrderList(@RequestBody Map<String, Object> map) {
		
		int U_ID = (int)map.get("U_ID");
		return oSMOrderingService.userOrderList(U_ID);
	}
	
	@PostMapping("/orderDetails")
	@ResponseBody
	public String orderDetails(@RequestBody Map<String, Object> map) {
		int O_ID = (int) map.get("O_ID");
		return oSMOrderDetailService.orderDetails(O_ID);
	}
}
