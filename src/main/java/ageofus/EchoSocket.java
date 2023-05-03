package ageofus;

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
    private static Set<ClientThread> clientThreads = new CopyOnWriteArraySet<>();


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress());
        sessions.add(session);
        System.out.println("Amount of connections: "+ sessions.size());
        // ---- Assigning a client -----
        ClientThread clientThread = new ClientThread(session);
        if(clientThreads.size() == 2){
            MatchHandler matchHandler = new MatchHandler();
            System.out.println("Clients is placed in match");
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


    private class ClientThread extends Thread{

        private Session session;

        public ClientThread(Session session){
            this.session = session;
            clientThreads.add(this);
            System.out.println(clientThreads.toString());
            System.out.println("Client has been assign thread");
            start();
        }

        public void run() {


        }
    }


    private class MatchHandler extends Thread{

        Object[] clientArray;
        ClientThread client1;
        ClientThread client2;
        public MatchHandler(){
            clientArray = clientThreads.toArray();
            client1 = (ClientThread) clientArray[0];
            client2 = (ClientThread) clientArray[1];
            
            //start();
        }


        public void run(){

        }

    }
}