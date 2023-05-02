package ageofus;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

@WebSocket
public class EchoSocket {


    private static Set<Session> sessions = new CopyOnWriteArraySet<>();






    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
        sessions.add(session);
        System.out.println("Amount of connections: "+ sessions.size());
        // ---- Starting thread for match-----
        if(sessions.size() >= 2){
            MatchHandler matchHandler = new MatchHandler();
            matchHandler.start();
        }

    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed with statusCode=" + statusCode + ", reason=" + reason);

    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }


    private class MatchHandler extends Thread{


        public void run(){
            System.out.println("You have found a match");
            onMessage("You have found a match");
        }

    }
}