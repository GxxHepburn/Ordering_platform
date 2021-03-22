package com.gxx.ordering_platform.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.gxx.ordering_platform.entity.Mer;
import com.gxx.ordering_platform.entity.Multi_Orders_Tab_Tabtype;
import com.gxx.ordering_platform.entity.OrderDetail;
import com.gxx.ordering_platform.entity.Orders;
import com.gxx.ordering_platform.entity.Printer;
import com.gxx.ordering_platform.entity.Tab;
import com.gxx.ordering_platform.entity.TabType;
import com.gxx.ordering_platform.entity.WxPayNotifyV0;
import com.gxx.ordering_platform.handler.OSMOrderingHandler;
import com.gxx.ordering_platform.mapper.MerMapper;
import com.gxx.ordering_platform.mapper.OrderAddMapper;
import com.gxx.ordering_platform.mapper.OrderDetailMapper;
import com.gxx.ordering_platform.mapper.OrderReturnDetailMapper;
import com.gxx.ordering_platform.mapper.OrderReturnMapper;
import com.gxx.ordering_platform.mapper.OrdersMapper;
import com.gxx.ordering_platform.mapper.PayMapper;
import com.gxx.ordering_platform.mapper.PrinterMapper;
import com.gxx.ordering_platform.mapper.RefundMapper;
import com.gxx.ordering_platform.mapper.TabMapper;
import com.gxx.ordering_platform.mapper.TabTypeMapper;
import com.gxx.ordering_platform.wxPaySDK.WXPayUtil;
import com.gxx.ordering_platform.xpyunSDK.service.PrintService;
import com.gxx.ordering_platform.xpyunSDK.util.Config;
import com.gxx.ordering_platform.xpyunSDK.vo.ObjectRestResponse;
import com.gxx.ordering_platform.xpyunSDK.vo.PrintRequest;

@Component
public class WxPayOtherService {
	
	@Autowired
	WechatOrderingService wechatOrderingService;
	
	@Autowired
	OrdersMapper ordersMapper;
	
	@Autowired
	RefundMapper refundMapper;
	
	@Autowired
	MerMapper merMapper;
	
	@Autowired
	PayMapper payMapper;
	
	@Autowired
	OrderReturnDetailMapper orderReturnDetailMapper;
	
	@Autowired
	OrderReturnMapper orderReturnMapper;
	
	@Autowired
	OrderDetailMapper orderDetailMapper;
	
	@Autowired
	OrderAddMapper orderAddMapper;
	
	@Autowired
	TabMapper tabMapper;
	
	@Autowired
	TabTypeMapper tabTypeMapper;
	
	@Autowired
	PrinterMapper printerMapper;
	
	@Autowired
	WxPayService wxPayService;
	
	@Autowired
	OSMOrderingHandler oSMOrderingHandler;
	
	@Value("${serviceNumber.mchKey}")
	private String mchKey;
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Transactional
	public String success(HttpServletRequest request, @RequestBody WxPayNotifyV0 param) {
		// 先判断是否处理过，如果处理过，就跳过
		Orders orders = ordersMapper.getOrderByO_OutTradeNo(param.getOut_trade_no());
		Mer mer = merMapper.getMerByMID(orders.getO_MID());
		if (orders.getO_PayStatue() == 0) {
			
			// 先判断支付成功还是失败，支付失败什么也不做,直接返回
			if ("FAIL".equals(param.getResult_code())) {
				
				// 判断支付顺序，如果是先支付后付款，则删除订单
				if (mer.getM_IsOrderWithPay() == 1) {
					this.deleteOrders(orders.getO_UniqSearchID());
				}
				
				Map<String, String> result = new HashMap<String, String>();
				if ("SUCCESS".equals(param.getReturn_code())) {
					result.put("return_code", "SUCCESS");
					result.put("return_msg", "OK");
				}
				String successReturn = null;
				try {
					successReturn =  WXPayUtil.mapToXml(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return successReturn;
			}
			
		
			//修改isPayNow，同时设置payStatus,payTime
			Date payTime = new Date();
			wxPayService.updatePaied(param.getOut_trade_no(), 0, 1, payTime);
			
			wxPayService.insertPay(param);
			
			// ，用websocket连接，发送语音播报，前台自动打印客人小票，如果是在接单页面，则刷新接单页面订单数据
			Multi_Orders_Tab_Tabtype multi_Orders_Tab_Tabtype = ordersMapper.getOrderWithTNameAndTTNameByO_OutTradeNo(param.getOut_trade_no());
			// 生成语音内容Json
			JSONObject wbssJsonObject = new JSONObject();
			wbssJsonObject.put("type", "1");
			String voiceString = multi_Orders_Tab_Tabtype.getTT_Name() + "," + multi_Orders_Tab_Tabtype.getT_Name() + "的客人支付" + (Float.valueOf(param.getTotal_fee())/100.00f) + "元";
			wbssJsonObject.put("voiceText", voiceString);
			wbssJsonObject.put("O_ID", multi_Orders_Tab_Tabtype.getO_ID());
			try {
				oSMOrderingHandler.sendTextMessage(multi_Orders_Tab_Tabtype.getO_MID(), wbssJsonObject.toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// 支付成功时，根据支付顺序，如果是先支付后下单，则多一步打印订单
			if (mer.getM_IsOrderWithPay() == 1) {
				// 创建打印所需对象
				PrintService printService = new PrintService();
				PrintRequest printRequest = new PrintRequest();
				String printContent = "";
				List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orders.getO_ID());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				orders = ordersMapper.getordersByO_ID(orders.getO_ID());
				String tname = "";
				String ttname = "";
				Tab tab = tabMapper.getByTabId(orders.getO_TID());
				if (tab != null) {
					TabType tabType = tabTypeMapper.getByTabTypeId(tab.getT_TTID());
					tname = tab.getT_Name();
					ttname = tabType.getTT_Name();
				}
				
				// 拼接小票内容
				// 餐厅名
				printContent+=("<CB>"+mer.getM_Name()+"<BR>");
				printContent+=("<L><N>--------------------------------"+"<BR>");
				printContent+=("<L><N>类型:  下单"+"<BR>");
				printContent+=("<L><N>单号:"+orders.getO_UniqSearchID()+"<BR>");
				printContent+=("<L><N>下单时间:"+format.format(orders.getO_OrderingTime())+"<BR>");
				String opt = "";
				if(orders.getO_PayTime() != null) {
					opt = format.format(orders.getO_PayTime());
				}
				printContent+=("<L><N>支付时间:"+opt+"<BR>");
				printContent+=("<L><N>餐桌区域:"+ttname+"<BR>");
				printContent+=("<L><N>餐桌:"+tname+"<BR>");
				printContent+=("<L><N>用餐人数:"+orders.getO_NumberOfDiners()+"<BR>");
				printContent+=("<L><N>--------------------------------"+"<BR>");
				printContent+=("<L><N>备注:"+orders.getO_Remarks()+"<BR>");
				printContent+=("<L><N>**************商品**************"+"<BR>");
				printContent+=("<BR>");
				
				for (int i = 0; i < orderDetails.size(); i++) {
					
					OrderDetail orderDetail = orderDetails.get(i);
					
					String OD_FName = orderDetail.getOD_FName();
					int realNum = orderDetail.getOD_RealNum();
					// 拼接小票菜品项
					printContent+=("<L><HB>"+OD_FName+"<BR>");
					printContent+=("<L><N> "+orderDetail.getOD_Spec()+" "+orderDetail.getOD_PropOne()+" "+orderDetail.getOD_PropTwo()+"<BR>");
					printContent+=("<R><N>x"+realNum+"   ￥"+orderDetail.getOD_RealPrice()+"<BR>");
				}
				
				// 拼接小票信息
				printContent+=("<L><N>--------------------------------"+"<BR>");
				printContent+=("<L><B>合计:￥"+orders.getO_TotlePrice()+"<BR>");
				printContent+=("<L><N>********************************"+"<BR>");
				printContent+=("<L><N>--------------------------------"+"<BR>");
				printContent+=("<L><N>打印时间:"+format.format(new Date())+"<BR>");
				
				// 获取打印机sn
				List<Printer> printers = printerMapper.getByMID(orders.getO_MID());
				// 设置打印内容
				printRequest.setContent(printContent);
				// 根据打印机数目，设置sn号，同时发送打印请求
				for (int i = 0; i < printers.size(); i++) {
					printRequest.setSn(printers.get(i).getP_No());
					Config.createRequestHeader(printRequest);
					ObjectRestResponse<String> result = printService.print(printRequest);
					logger.info(result.toString());
				}
			}
		}
		Map<String, String> result = new HashMap<String, String>();
		if ("SUCCESS".equals(param.getReturn_code())) {
			result.put("return_code", "SUCCESS");
			result.put("return_msg", "OK");
		}
		String successReturn = null;
		try {
			successReturn =  WXPayUtil.mapToXml(result);
		} catch (Exception e) {
			logger.error("ERROR", e);
		}
		return successReturn;
	}
	
	// 删除订单方法
	@Transactional
	public void deleteOrders(String searchId) {
		Orders orders = ordersMapper.getOrdersByUniqSearchID(searchId);
		int oid = orders.getO_ID();
		// 删除退款表中R_OID = oid
		refundMapper.deleteByOID(oid);
		// 删除支付表中P_OID = oid
		payMapper.deleteByOID(oid);
		// 删除orderreturndetail 中ORD_OID = oid
		orderReturnDetailMapper.deleteByOID(oid);
		// 删除orderreturn 中OR_OID = oid
		orderReturnMapper.deleteByOID(oid);
		// 删除orderdetail 中OD_OID = oid
		orderDetailMapper.deleteByOID(oid);
		// 删除orderadd 中 OA_OID = oid
		orderAddMapper.deleteByOID(oid);
		// 删除orders中 O_ID = oid
		ordersMapper.deleteByOID(oid);
	}
}
