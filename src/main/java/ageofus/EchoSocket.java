package ageofus;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebSocket
public class EchoSocket {

    private Set<Session> sessions = Collections.synchronizedSet(new HashSet<Session>());

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
        sessions.add(session);

        Thread thread = new Thread(new WebSocketHandler(session));
        thread.start();
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Connection closed with statusCode=" + statusCode + ", reason=" + reason);
        sessions.clear(); // todo ta bara bort specifica sessionen inte alla.
        if(sessions.isEmpty()){
            System.out.println("du tömde skiten");
        }
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
    }

    private class WebSocketHandler extends Thread{

        private Session session;

        public WebSocketHandler(Session session){
            this.session=session;
        }

        public void run(){
            while(session.isOpen()){
                try{
                    System.out.println("Ny tråd skapad");
                    if(!(sessions.isEmpty())){
                        System.out.println("Finns en session i listan");
                    }
                    Thread.sleep(10000);
                } catch (InterruptedException ie){
                    ie.printStackTrace();;
                }
            }
        }
    }

    private class MatchHandler{

        public MatchHandler(){
            sessions.clear();
        }
    }
}