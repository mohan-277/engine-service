package com.mohan.gameengineservice.websocket.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mohan.gameengineservice.dto.MatchDetailsDTO;
import com.mohan.gameengineservice.websocket.WebSocketSessionManager;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class EchoWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();


    private final WebSocketSessionManager sessionManager;




    @Autowired
    public EchoWebSocketHandler(WebSocketSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Autowired
    private TestSimulation testSimulation;

    @Override
    public void afterConnectionEstablished( @Nonnull  WebSocketSession session) throws Exception {
        if (sessionManager != null) {
            sessionManager.addSession(session);
        } else {
            // Handle the case where sessionManager is null
            throw new IllegalStateException("sessionManager is not initialized.");
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Extract the payload (message content) from the TextMessage object
        String payload = message.getPayload();

        // Convert the JSON payload to a MatchDetailsDTO object
        MatchDetailsDTO matchDTO = parseMatchDetails(payload);

        // Ensure matchDTO is not null before proceeding
        if (matchDTO == null) {
            sendErrorMessage(session, "Invalid match details.");
            return;
        }

        // Start the match simulation with the provided match details
        try {
            testSimulation.simulateMatchFromDTO(matchDTO);
        } catch (InterruptedException e) {
            e.printStackTrace();
            sendErrorMessage(session, "Error occurred during match simulation.");
        }
    }

    // Helper method to parse the JSON payload into a MatchDetailsDTO object
    private MatchDetailsDTO parseMatchDetails(String payload) {
        try {
            // Assuming you are using Jackson for JSON processing
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(payload, MatchDetailsDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if parsing fails
        }
    }

    // Helper method to send an error message to the client
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            session.sendMessage(new TextMessage(errorMessage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
