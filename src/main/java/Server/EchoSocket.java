package Server;

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
    private static Set<Client> clients = new CopyOnWriteArraySet<>();


    @OnWebSocketConnect
    public void onConnect(Session session) { //When client connects to the server
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress()); //Prints out the client ID
        sessions.add(session); //Adds session to a list
        System.out.println("Amount of connections: "+ sessions.size());
        // ---- Assigning a client -----
        Client clientThread = new Client(session); //Starting a thread for the client
        if(clients.size() == 2){ // If there is two clients in the clientThreads list
            MatchHandler matchHandler = new MatchHandler(); //Start a match thread
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

    private class MatchHandler extends Thread{

        Object[] clientArray;
        Client client1;
        Client client2;


        public MatchHandler(){
            clientArray = clients.toArray(); //Creates a array from the set-list
            client1 = (Client) clientArray[0]; //assigns the first client in the match
            client2 = (Client) clientArray[1]; //assigns the second client in the match

            //This part removes the first two clients from the clientList, which lets the other clients in this list to take there place to then be assigned a match.
            clients.remove(client1); //Removes the first client in the list
            clients.remove(client2); //Removes the second client in the list

            start(); //Start the match thread
        }

        public void run(){
            ArrayList<String> list = new ArrayList<String>();
            list.add("str1");
            list.add("str2");
            list.add("str3");
            String json1 = "found";

            try {
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            client1.sendJson(json1);
            client2.sendJson(json1);

            try {
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            for (int i = 0; i < 100; i++){

                String jsonBorn = "{ \"method\": \"update\",\"game\": [{ \"team\": 0,\"id\":1,\"pos\":"+i+"}]}";
                System.out.println(jsonBorn);
                client1.sendJson(jsonBorn);
                client2.sendJson(jsonBorn);
                try {
                    sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }
}