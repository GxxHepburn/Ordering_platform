package com.gxx.ordering_platform.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import org.springframework.transaction.annotation.Transactional;
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
import com.gxx.ordering_platform.service.OSMPayService;
import com.gxx.ordering_platform.service.OSMRefundService;
import com.gxx.ordering_platform.service.OSMTabService;
import com.gxx.ordering_platform.service.OSMTabTypeService;
import com.gxx.ordering_platform.service.OSMUsersService;
import com.gxx.ordering_platform.service.OSMWechatUserService;
import com.gxx.ordering_platform.utils.RedisUtil;


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
	
	@Autowired OSMWechatUserService oSMWechatUserService;
	
	@Autowired OSMPayService oSMPayService;
	
	@Autowired OSMRefundService oSMRefundService;
	
	@PostMapping("/login")
	@ResponseBody
	public String login(@RequestBody Map<String, Object> map) {
		try {
			return mmaService.login(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@GetMapping("/menus")
	@ResponseBody
	public String menus() {
		return this.mensJsonString;
	}
	
	@PostMapping(value = "/users", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String users(@RequestBody Map<String, Object> map){
		
		try {
			return oSMUsersService.users(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping("/orderDetails")
	@ResponseBody
	public String orderDetails(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderDetailService.orderDetails(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/uploadFoodImg", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String uploadFoodImg(@RequestParam("file") MultipartFile file) {
		try {
			return oSMFoodService.uploadFoodImg(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("ERROR", e);
		}
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/cates", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String cates(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.cates(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/editFood", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String editFood(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.editFood(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/deleteFood", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteFood(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.deleteFood(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/addFood", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addFood(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.addFood(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchGoods", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchGoods(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.searchGoods(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchCates", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchCates(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.searchCates(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/deleteCate", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteCate(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.deleteCate(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/changeFTName", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeFTName(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.changeFTName(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/addFT", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addFT(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.addFT(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getMerInfo", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getMerInfo(@RequestBody Map<String, Object> map) {
		try {
			return oSMMerService.getMerInfo(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/uploadMerImg", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String uploadMerImg(@RequestParam("file") MultipartFile file) {
		return oSMMerService.uploadMerImg(file);
	}
	
	@PostMapping(value = "/changeMerInfo", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeMerInfo(@RequestBody Map<String, Object> map) {
		try {
			return oSMMerService.changeMerInfo(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/tabs", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String tabs(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.tabs(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/deleteTab", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteTab(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.deleteTab(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/tabtypes", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String tabtypes(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabTypeService.tabtypes(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/editTab", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String editTab(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.editTab(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchtabs", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchtabs(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.searchtabs(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/addTab", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addTab(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.addTab(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchTabTypes", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchTabTypes(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabTypeService.searchTabTypes(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/deleteTabType", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String deleteTabType(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabTypeService.deleteTabType(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/changeTTName", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeTTName(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabTypeService.changeTTName(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/addTT", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String addTT(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabTypeService.addTT(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/ordersTabAndTabTypeOptions", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String ordersTabAndTabTypeOptions(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.ordersTabAndTabTypeOptions(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderForm", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderForm(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderForm(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderAddFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderAddFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderAddFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/onlyReturnGoods", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String onlyReturnGoods(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.onlyReturnGoods(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/onePunchUpGoods", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String onePunchUpGoods(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.onePunchUpGoods(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/changeFoodStatue", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeFoodStatue(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.changeFoodStatue(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/onePunchDisableOrAble", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String onePunchDisableOrAble(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodService.onePunchDisableOrAble(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/changeWechatUserStatus", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeWechatUserStatus(@RequestBody Map<String, Object> map) {
		try {
			return oSMWechatUserService.changeWechatUserStatus(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderPayForm", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderPayForm(@RequestBody Map<String, Object> map) {
		try {
			return oSMPayService.getOrderPayForm(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/takingOrder", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String takingOrder(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.takingOrder(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/notTakingOrerAddFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String notTakingOrerAddFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.notTakingOrerAddFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/orderFiUnderLine", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String orderFiUnderLine(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.orderFiUnderLine(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/orderNotFiUnderLine", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String orderNotFiUnderLine(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.orderNotFiUnderLine(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/sendCheck", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String sendCheck(@RequestBody Map<String, Object> map) {
		try {
			return mmaService.sendCheck(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/realCheck", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String realCheck(@RequestBody Map<String, Object> map) {
		try {
			return mmaService.realCheck(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/returnGoodsWithMoney", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String returnGoodsWithMoney(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.returnGoodsWithMoney(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "供应商技术错误！请核实系统是否退款后，再谨慎人工退款，防止重复退款，因操作不当造成损失，商家自负!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getOrderReturnFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getOrderReturnFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getOrderReturnFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/checkTradePassword", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String checkTradePassword(@RequestBody Map<String, Object> map) {
		try {
			return mmaService.checkTradePassword(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getRefundFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getRefundFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMRefundService.getRefundFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/refundQuery", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String refundQuery(@RequestBody Map<String, Object> map) {
		try {
			return oSMRefundService.refundQuery(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getLastOrderFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getLastOrderFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getLastOrderFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getNotPayReturnAndNotFiAndFiOrderFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getReturnAndNotFiAndFiOrderFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.getNotPayReturnAndNotFiAndFiOrderFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getPayFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getPayFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMPayService.getPayFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/getRefundRecordFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String getRefundRecordFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMRefundService.getRefundRecordFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchOrdersPHour", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchOrdersPHour(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.searchOrdersPHour(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchOrdersPDay", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchOrdersPDay(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.searchOrdersPDay(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchOrdersPMonth", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchOrdersPMonth(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.searchOrdersPMonth(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchRefundPMonth", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchRefundPMonth(@RequestBody Map<String, Object> map) {
		try {
			return oSMRefundService.searchRefundPMonth(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/pSSGoodsAndGoodstypeOptions", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String pSSGoodsAndGoodstypeOptions(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.pSSGoodsAndGoodstypeOptions(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchPSSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchPSSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.searchPSSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchCSSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchCSSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.searchCSSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchPSCFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchPSCFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMFoodTypeService.searchPSCFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchRCFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchRCFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMRefundService.searchRCFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}

	@PostMapping(value = "/searchRSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchRSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMRefundService.searchRSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchCOSNFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchCOSNFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.searchCOSNFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchTRFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchTRFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.searchTRFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchTRWFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchTRWFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.searchTRWFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchTRMFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchTRMFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMTabService.searchTRMFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchUDSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchUDSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMUsersService.searchUDSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchNUSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchNUSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMUsersService.searchNUSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchCDFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchCDFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMUsersService.searchCDFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchCSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchCSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMUsersService.searchCSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchSDFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchSDFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.searchSDFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchRS2FormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchRS2FormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.searchRS2FormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/searchBSFormList", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String searchBSFormList(@RequestBody Map<String, Object> map) {
		try {
			return oSMOrderingService.searchBSFormList(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/changeMerOperateStatus", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeMerOperateStatus(@RequestBody Map<String, Object> map) {
		try {
			return oSMMerService.changeMerOperateStatus(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/changeMerIsOrderWithPay", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String changeMerIsOrderWithPay(@RequestBody Map<String, Object> map) {
		try {
			return oSMMerService.changeMerIsOrderWithPay(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/sendChangePWCheck", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String sendChangePWCheck(@RequestBody Map<String, Object> map) {
		try {
			return mmaService.sendChangePWCheck(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
	
	@PostMapping(value = "/realChangePWCheck", produces="application/json;charset=UTF-8")
	@ResponseBody
	public String realChangePWCheck(@RequestBody Map<String, Object> map) {
		try {
			return mmaService.realChangePWCheck(map);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		// 错误信息
		JSONObject newJsonObject = new JSONObject();
		
		JSONObject metaJsonObject = new JSONObject();
		metaJsonObject.put("status", 500);
		metaJsonObject.put("msg", "服务器错误，请联系管理员!");
		
		newJsonObject.put("meta", metaJsonObject);
		return newJsonObject.toString();
	}
}
