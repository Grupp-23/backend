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
            MatchHandler matchHandler = new MatchHandler();
            System.out.println("Client is placed in match");
        }
    }


    private class MatchHandler extends Thread{

        public MatchHandler(){
            start();
        }
        public void assignPairs(ArrayList<ClientThread> clientThreads){

            ArrayList<ClientThread> clientPairs = new ArrayList<>();
            while (clientPairs.size() < 2){
                for (int i = 0; i < clientThreads.size(); i++){
                    clientPairs.add(clientThreads.get(i));
                }
            }

        }

        public void run(){

        }

    }
}