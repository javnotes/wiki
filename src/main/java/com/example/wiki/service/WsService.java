package com.example.wiki.service;

import com.example.wiki.websocket.WebSocketServer;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author luf
 * @date 2023/03/26 16:13
 **/

@Service
public class WsService {

    @Resource
    WebSocketServer webSocketServer;

    /**
     * 异步发送消息
     */
    @Async
    public void sendInfo(String message, String logId) {
        // 因为是个异步方法，所以需要手动传递日志流水号，否则日志无法关联
        MDC.put("LOG_ID", logId);
        webSocketServer.sendInfo(message);
    }
}
