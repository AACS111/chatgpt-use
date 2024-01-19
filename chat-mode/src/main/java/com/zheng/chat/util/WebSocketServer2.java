package com.zheng.chat.util;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.zheng.chat.test.WebChatGPT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.util.ApplicationContextTestUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----web---
 * @date 2024/1/9 19:41:37
 */
@Slf4j
@Service
@ServerEndpoint("/websocket/{uid}")
@Component
public class WebSocketServer2 {

    //连接建立时长
    private static final long sessionTimeout = 60000000;

    // 用来存放每个客户端对应的WebSocketServer对象
    private static Map<String, WebSocketServer2> webSocketMap = new ConcurrentHashMap<>();

    // 与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    // 接收id
    private String uid;

    private static WebChatGPT webChatGPT;


    static {
         webChatGPT = SpringUtil.getBean(WebChatGPT.class);
    }

    /**
     * 连接建立成功调用的方法
     * @author zhengfuping
     * @date 2023/8/22
     * @param session
     * @param uid
     */
    @OnOpen
    public void onOpen(Session session , @PathParam("uid") String uid){
        session.setMaxIdleTimeout(sessionTimeout);
        this.session = session;
        this.uid = uid;
        if (webSocketMap.containsKey(uid)){
            webSocketMap.remove(uid);
        }
        webSocketMap.put(uid,this);
        log.info("websocket连接成功编号uid: " + uid + "，当前在线数: " + getOnlineClients());

        try{
            // 响应客户端实际业务数据！
            sendMessage("conn_success");
        }catch (Exception e){
            log.error("websocket发送连接成功错误编号uid: " + uid + "，网络异常!!!");
        }
    }

    /**
     * 连接关闭调用的方法
     * @author zhengfuping
     * @date 2023/8/22
     */
    @OnClose
    public void onClose(){
        try {
            if (webSocketMap.containsKey(uid)){
                webSocketMap.remove(uid);
            }
            log.info("websocket退出编号uid: " + uid + "，当前在线数为: " + getOnlineClients());
        } catch (Exception e) {
            log.error("websocket编号uid连接关闭错误: " + uid + "，原因: " + e.getMessage());
        }
    }


    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        try {
//            sendInfo(message);
//            try {
                JSON parse = JSONUtil.parse(message);
                String msg = parse.getByPath("msg").toString();

             webChatGPT.getAnswer2(session,msg);
//            flux.subscribe();
            log.info("websocket收到客户端编号uid消息: " + uid + ", 报文: " );

        } catch (Exception e) {
            log.error("websocket发送消息失败编号uid为: " + uid + ",报文: " + message);
        }

    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("websocket编号uid错误: " + this.uid + "原因: " + error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     * @author yingfeng
     * @date 2023/8/22 10:11
     * @Param * @param null
     * @return
     */

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 获取客户端在线数
     * @author zhengfuping
     * @date 2023/8/22 10:11
     * @param
     */
    public static synchronized int getOnlineClients() {
        if (Objects.isNull(webSocketMap)) {
            return 0;
        } else {
            return webSocketMap.size();
        }
    }

    /**
     * 群发自定义消息
     * @author zhengfuping
     * @date 2023/8/22 9:52
     * @param message
     */
//    public  void sendInfo(String message) {
//        webSocketMap.forEach((k, v) -> {
//            WebSocketServer2 webSocketServer = webSocketMap.get(k);
//            try {
//                JSON parse = JSONUtil.parse(message);
//                String msg = parse.getByPath("msg").toString();
//
////                String answer = webChatGPT.getAnswer(session,msg);
////                webSocketServer.sendMessage(answer);
//                log.info("websocket群发消息编号uid为: " + k + "，消息: " + message);
//            } catch (IOException e) {
//                log.error("群发自定义消息失败: " + k + "，message: " + message);
//            }
//        });
//    }
    /**
     * 服务端群发消息-心跳包
     * @author zhengfuping
     * @date 2023/8/22 10:09
     * @param message 推送数据
     * @return int 连接数
     */
    public static synchronized int sendPing(String message){
        if (webSocketMap.size() == 0)
            return 0;
        StringBuffer uids = new StringBuffer();
        AtomicInteger count = new AtomicInteger();
        webSocketMap.forEach((uid,server)->{
            count.getAndIncrement();

            if (webSocketMap.containsKey(uid)){
                WebSocketServer2 webSocketServer = webSocketMap.get(uid);
                try {
                    if (Integer.valueOf(uid) ==101){
                        Integer i=1/0;
                    }

                    webSocketServer.sendMessage(message);
                    if (count.equals(webSocketMap.size() - 1)){
                        uids.append("uid");
                        return;

                    }
                    uids.append(uid).append(",");
                } catch (Exception e) {
                    webSocketMap.remove(uid);
                    log.info("客户端心跳检测异常移除: " + uid + "，心跳发送失败，已移除！");

                }
            }else {
                log.info("客户端心跳检测异常不存在: " + uid + "，不存在！");

            }
        });
        log.info("客户端心跳检测结果: " + uids + "连接正在运行");
        return webSocketMap.size();
    }
    /**
     * 连接是否存在
     * @param uid
     * @return boolean
     */
    public static boolean isConnected(String uid) {
        if (Objects.nonNull(webSocketMap) && webSocketMap.containsKey(uid)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 单机使用，外部接口通过指定的客户id向该客户推送消息
     * @param key
     * @param message
     * @return boolean
     */
    public static boolean sendMessageByWayBillId( String key, String message) {
        WebSocketServer2 webSocketServer = webSocketMap.get(key);
        if (Objects.nonNull(webSocketServer)) {
            try {
                webSocketServer.sendMessage(message);
                log.info("websocket发送消息编号uid为: " + key + "发送消息: " + message);
                return true;
            } catch (Exception e) {
                log.error("websocket发送消息失败编号uid为: " + key + "消息: " + message);
                return false;
            }
        } else {
            log.error("websocket未连接编号uid号为: " + key + "消息: " + message);
            return false;
        }
    }

}

