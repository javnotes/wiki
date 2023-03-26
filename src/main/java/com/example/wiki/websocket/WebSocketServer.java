package com.example.wiki.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;

/**
 * WebSocket服务端，用于接收客户端的连接、接收消息、发送消息，关闭连接、错误处理等，本例中只实现了发送消息
 * 发送消息时可以指定客户端，也可以广播，即发送给所有客户端，本例中只实现了广播
 */
@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketServer.class);

    // 每个客户端一个token，用于标识唯一的客户端
    private String token = "";

    // 用于存放每个客户端对应的WebSocketServer对象
    private static HashMap<String, Session> map = new HashMap<>();

    /**
     * 连接成功
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        // 将token和session存入map，推送信息需要session，所以需要将session保存起来
        map.put(token, session);
        // 将token保存起来，用于发送消息
        this.token = token;
        LOG.info("有新连接：token：{}，session id：{}，当前连接数：{}", token, session.getId(), map.size());
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose(Session session) {
        // 移除map中的session
        map.remove(this.token);
        LOG.info("连接关闭，token：{}，session id：{}！当前连接数：{}", this.token, session.getId(), map.size());
    }

    /**
     * 收到消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LOG.info("收到消息：{}，内容：{}", token, message);
    }

    /**
     * 连接错误
     */
    @OnError
    public void onError(Session session, Throwable error) {
        LOG.error("发生错误", error);
    }

    /**
     * 群发消息
     */
    public void sendInfo(String message) {
        // 遍历map，发送消息
        for (String token : map.keySet()) {
            // 获取每个客户端对应的WebSocketServer对象
            Session session = map.get(token);
            try {
                // 发送消息，getBasicRemote()和getAsyncRemote()的区别是getBasicRemote()是同步的，getAsyncRemote()是异步的，本例中使用同步
                // sendText()和sendBinary()的区别是sendText()是发送文本，sendBinary()是发送二进制数据
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                LOG.error("推送消息失败：{}，内容：{}", token, message);
            }
            LOG.info("推送消息：{}，内容：{}", token, message);
        }
    }

}
