package Server;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

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
        Object[] sessionArray;
        Session session1;
        Session session2;
        public MatchHandler(){
            sessionArray = sessions.toArray();
            session1 = (Session) sessionArray[0];
            session2 = (Session) sessionArray[1];
            start();
        }
        
        public void run(){
            System.out.println("Two players have joined, they have started a match");
            if (session1 != null){
                System.out.println("Session 1 is connected");
            }
            if (session2 != null){
                System.out.println("Session 2 is connected");
            }
        }

    }
}