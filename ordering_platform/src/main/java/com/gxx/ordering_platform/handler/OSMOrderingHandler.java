package com.gxx.ordering_platform.handler;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.gxx.ordering_platform.service.OSMMerService;

@Component
public class OSMOrderingHandler extends TextWebSocketHandler {
	
	@Autowired OSMMerService oSMMerService;
	
	// 保存所有Client的WebSocket会话实例:
    private Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();
    
    public Map<String, WebSocketSession> getClients() {
		return clients;
	}

	@Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 新会话根据ID放入Map:
        
     // 检查是否有商户重乎链接-name属性重复,有就关闭
		for (String id : clients.keySet()) {
			WebSocketSession sion = clients.get(id);
			if (sion.getAttributes().get("name").equals(session.getAttributes().get("name"))) {
				sion.close();
			}
		}

        clients.put(session.getId(), session);
        // 让商家管理系统上线
//        oSMMerService.openMer(session.getAttributes().get("name"));
        System.out.println("open wbss " + clients.size());
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 让商家管理系统下线
//    	oSMMerService.closeMer(session.getAttributes().get("name"));
        clients.remove(session.getId());
    }

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// TODO Auto-generated method stub
		String s = message.getPayload();
		System.out.println(s);
		System.out.println(clients.size());
		String r = "服务器返回接收到的数据： " + s + "-" + LocalDateTime.now();
		session.sendMessage(new TextMessage(r));
		super.handleTextMessage(session, message);
	}
}
