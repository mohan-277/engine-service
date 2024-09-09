package com.mohan.gameengineservice.websocket;


import jakarta.annotation.Nonnull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.http.WebSocket;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Configuration
@EnableWebSocket
public class MatchWebSocketHandler extends TextWebSocketHandler {


    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    @Override
    public void afterConnectionEstablished(@Nonnull WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(@Nonnull  WebSocketSession session,  @Nonnull CloseStatus status) throws Exception {
        sessions.remove(session);
    }

    @Override
    protected void handleTextMessage(@Nonnull  WebSocketSession session, TextMessage message) throws IOException {
        String payload = message.getPayload();
        // Example: Handle incoming message
        if ("start".equalsIgnoreCase(payload)) {
            sendMatchUpdate("Match has started!");
        }
        // Additional handling logic
    }

    public void sendMatchUpdate(String update) {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(update));
                } catch (IOException e) {
                    e.printStackTrace(); // Handle the exception appropriately
                }
            }
        }
    }

}
