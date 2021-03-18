package com.gxx.ordering_platform.handler;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.gxx.ordering_platform.entity.Mmngct;
import com.gxx.ordering_platform.mapper.MmaMapper;
import com.gxx.ordering_platform.service.OSMMerService;

@Component
public class OSMOrderingHandler extends TextWebSocketHandler {
	
	final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired OSMMerService oSMMerService;
	
	@Autowired
	MmaMapper mmaMapper;
	
	// 保存所有Client的WebSocket会话实例:
    private Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    
    public Map<String, WebSocketSession> getClients() {
		return clients;
	}

	@Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 新会话根据ID放入Map:
        
		Mmngct mmngct = mmaMapper.getByUsername(session.getAttributes().get("name").toString());
		session.getAttributes().put("M_ID", mmngct.getMMA_MID());
        clients.put(session.getId(), session);
        // 让商家管理系统上线
        oSMMerService.openMer(Integer.valueOf(session.getAttributes().get("M_ID").toString()));
        logger.info("open wbss " + clients.size());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 让商家管理系统下线
    	oSMMerService.closeMer(Integer.valueOf(session.getAttributes().get("M_ID").toString()));
        clients.remove(session.getId());
        logger.info("closed wbss " + session.getId() + ", ");
    }

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TODO Auto-generated method stub
		String s = message.getPayload();
		JSONObject msgJsonObject = new JSONObject(s);
		JSONObject textJsonObject = new JSONObject();
		// 响应心跳包
		if ("0".equals(msgJsonObject.get("type").toString())) {
			String r = "服务器返回接收到的心跳包，时间： " + "-" + LocalDateTime.now();
			textJsonObject.put("type", "0");
			textJsonObject.put("text", r);
		} else if ("3".equals(msgJsonObject.get("type").toString())) {
			String r = "连接测试正常 ";
			textJsonObject.put("type", "3");
			textJsonObject.put("voiceText", r);
			logger.info("wbss hadleTextMessage: " + textJsonObject.toString());
		}
		session.sendMessage(new TextMessage(textJsonObject.toString()));
		super.handleTextMessage(session, message);
	}
	
	public void sendTextMessage(int M_ID, String text) throws Exception {
		// 先检查有没有这个session，如果有就发送，如果没有，则什么也不做
		for (String id : clients.keySet()) {
			WebSocketSession sion = clients.get(id);
			if (sion.getAttributes().get("M_ID").toString().equals("" + M_ID)) {
				sion.sendMessage(new TextMessage(text));
			}
		}
		
	}
}
