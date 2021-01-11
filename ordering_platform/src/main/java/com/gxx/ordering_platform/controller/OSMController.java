package com.gxx.ordering_platform.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.WechatUserMapper;
import com.gxx.ordering_platform.service.MmaService;
import com.gxx.ordering_platform.service.OSMFoodService;
import com.gxx.ordering_platform.service.OSMFoodTypeService;
import com.gxx.ordering_platform.service.OSMMerService;
import com.gxx.ordering_platform.service.OSMOrderDetailService;
import com.gxx.ordering_platform.service.OSMOrderingService;
import com.gxx.ordering_platform.service.OSMTabService;
import com.gxx.ordering_platform.service.OSMTabTypeService;
import com.gxx.ordering_platform.service.OSMUsersService;

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
	
	@Autowired OSMFoodService oSMFoodService;
	
	@Autowired OSMFoodTypeService oSMFoodTypeService;
	
	@Autowired OSMMerService oSMMerService;
	
	@Autowired OSMTabService oSMTabService;
	
	@Autowired OSMTabTypeService oSMTabTypeService;
	
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
		
		try {
			return oSMUsersService.users(query, pagenum, pagesize, mmngctUserName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "获取失败");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping("/orderDetails")
	@ResponseBody
	public String orderDetails(@RequestBody Map<String, Object> map) {
		int O_ID = Integer.valueOf(map.get("O_ID").toString());
		return oSMOrderDetailService.orderDetails(O_ID);
	}
	
	@PostMapping("/goods")
	@ResponseBody
	public String goods(@RequestBody Map<String, Object> map) {
		return oSMFoodService.goods(map);
	}
	
	@PostMapping(value = "/uploadFoodImg", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String uploadFoodImg(@RequestParam("file") MultipartFile file) {
		return oSMFoodService.uploadFoodImg(file);
	}
	
	@PostMapping(value = "/cates", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String cates(@RequestBody Map<String, Object> map) {
		return oSMFoodTypeService.cates(map);
	}
	
	@PostMapping(value = "/editFood", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String editFood(@RequestBody Map<String, Object> map) {
		return oSMFoodService.editFood(map);
	}
	
	@PostMapping(value = "/deleteFood", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteFood(@RequestBody Map<String, Object> map) {
		return oSMFoodService.deleteFood(map);
	}
	
	@PostMapping(value = "/addFood", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addFood(@RequestBody Map<String, Object> map) {
		return oSMFoodService.addFood(map);
	}
	
	@PostMapping(value = "/searchGoods", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchGoods(@RequestBody Map<String, Object> map) {
		return oSMFoodService.searchGoods(map);
	}
	
	@PostMapping(value = "/searchCates", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchCates(@RequestBody Map<String, Object> map) {
		return oSMFoodTypeService.searchCates(map);
	}
	
	@PostMapping(value = "/deleteCate", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteCate(@RequestBody Map<String, Object> map) {
		return oSMFoodTypeService.deleteCate(map);
	}
	
	@PostMapping(value = "/changeFTName", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeFTName(@RequestBody Map<String, Object> map) {
		return oSMFoodTypeService.changeFTName(map);
	}
	
	@PostMapping(value = "/addFT", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addFT(@RequestBody Map<String, Object> map) {
		return oSMFoodTypeService.addFT(map);
	}
	
	@PostMapping(value = "/getMerInfo", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getMerInfo(@RequestBody Map<String, Object> map) {
		return oSMMerService.getMerInfo(map);
	}
	
	@PostMapping(value = "/uploadMerImg", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String uploadMerImg(@RequestParam("file") MultipartFile file) {
		return oSMMerService.uploadMerImg(file);
	}
	
	@PostMapping(value = "/changeMerInfo", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeMerInfo(@RequestBody Map<String, Object> map) {
		return oSMMerService.changeMerInfo(map);
	}
	
	@PostMapping(value = "/tabs", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String tabs(@RequestBody Map<String, Object> map) {
		return oSMTabService.tabs(map);
	}
	
	@PostMapping(value = "/deleteTab", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteTab(@RequestBody Map<String, Object> map) {
		return oSMTabService.deleteTab(map);
	}
	
	@PostMapping(value = "/tabtypes", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String tabtypes(@RequestBody Map<String, Object> map) {
		return oSMTabTypeService.tabtypes(map);
	}
	
	@PostMapping(value = "/editTab", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String editTab(@RequestBody Map<String, Object> map) {
		return oSMTabService.editTab(map);
	}
	
	@PostMapping(value = "/searchtabs", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchtabs(@RequestBody Map<String, Object> map) {
		return oSMTabService.searchtabs(map);
	}
	
	@PostMapping(value = "/addTab", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addTab(@RequestBody Map<String, Object> map) {
		return oSMTabService.addTab(map);
	}
	
	@PostMapping(value = "/searchTabTypes", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchTabTypes(@RequestBody Map<String, Object> map) {
		return oSMTabTypeService.searchTabTypes(map);
	}
	
	@PostMapping(value = "/deleteTabType", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteTabType(@RequestBody Map<String, Object> map) {
		return oSMTabTypeService.deleteTabType(map);
	}
	
	@PostMapping(value = "/changeTTName", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeTTName(@RequestBody Map<String, Object> map) {
		return oSMTabTypeService.changeTTName(map);
	}
	
	@PostMapping(value = "/addTT", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addTT(@RequestBody Map<String, Object> map) {
		return oSMTabTypeService.addTT(map);
	}
	
	@PostMapping(value = "/ordersTabAndTabTypeOptions", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ordersTabAndTabTypeOptions(@RequestBody Map<String, Object> map) {
		return oSMTabService.ordersTabAndTabTypeOptions(map);
	}
	
	@PostMapping(value = "/getOrderFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderFormList(map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "获取失败");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderForm", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderForm(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderForm(map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "获取失败");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderAddFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderAddFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderAddFormList(map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "获取失败");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/onlyReturnGoods", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String onlyReturnGoods(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.onlyReturnGoods(map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "获取失败");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/onePunchUpGoods", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String onePunchUpGoods(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.onePunchUpGoods(map);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "获取失败");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
}
