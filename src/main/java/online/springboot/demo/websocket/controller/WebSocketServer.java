package online.springboot.demo.websocket.controller;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketServer {

	private static int onlineCount = 0;

	private static CopyOnWriteArraySet<WebSocketServer> websocketSet = new CopyOnWriteArraySet<WebSocketServer>();

	private Session session;

	private final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		websocketSet.add(this); // ����set��
		addOnlineCount(); // ��������1
		log.info("�������Ӽ��룡��ǰ��������Ϊ" + getOnlineCount());
		try {
			sendMessage("���ӳɹ�");
		} catch (IOException e) {
			log.error("websocket IO�쳣");
		}
	}

	@OnClose
	public void onClose() {
		websocketSet.remove(this); // ��set��ɾ��
		subOnlineCount(); // ��������1
		log.info("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		log.info("���Կͻ��˵���Ϣ:" + message);

		// Ⱥ����Ϣ
		for (WebSocketServer item : websocketSet) {
			try {
				item.sendMessage(message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		log.error("��������");
		error.printStackTrace();
	}
	
	public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
 
 
    public void sendInfo(String message) throws IOException {
    	log.info(message);
        for (WebSocketServer item : websocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }
 
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
 
    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }
 
    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}
