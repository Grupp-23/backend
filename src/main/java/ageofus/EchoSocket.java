package ageofus;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.*;

@WebSocket
public class EchoSocket {

    private Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());
    private List<Session> sessionsList = Collections.synchronizedList(new ArrayList<>());
    private Object lock = new Object();
    private volatile int counter = 0;

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
        sessions.add(session);
        sessionsList.add(session);

        Thread thread = new Thread(new WebSocketHandler(session));
        thread.start();
        synchronized (lock){

            System.out.println(counter);
            if(sessionsList.size() >=2){
                Thread matchThread = new MatchHandler();
                matchThread.start();
            }
        }

    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed with statusCode=" + statusCode + ", reason=" + reason);
        sessions.remove(session); // todo ta bara bort specifica sessionen inte alla.
        if(!(sessions.contains(session))){
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
                    synchronized (lock) {
                        counter++;
                    }
                    System.out.println("Ny tråd skapad");
                    if(!(sessions.isEmpty())){
                        System.out.println("Finns en session i listan");
                        System.out.println(sessionsList.size());
                    }

                    Thread.sleep(10000);
                } catch (InterruptedException ie){
                    ie.printStackTrace();;
                }
            }
        }
    }

    private class MatchHandler extends Thread{

        private List<Session> matchThread =  Collections.synchronizedList(new ArrayList<>());

        public void run(){


                if (sessionsList.size() >= 2) {
                    Session session1 = sessionsList.get(0);
                    Session session2 = sessionsList.get(1);
                    System.out.println("Är denna startad? ");
                    try {
                        session1.getRemote().sendString("Match found! You are matched with another player.");
                        session2.getRemote().sendString("Match found! You are matched with another player.");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    sessionsList.remove(0);
                    sessionsList.remove(0);
                }

        }

    }
}