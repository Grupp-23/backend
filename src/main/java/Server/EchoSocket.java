package Server;

import Model.GameManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
    private static Set<ClientThread> clientThreads = new CopyOnWriteArraySet<>();

    private static HashMap<ClientThread, MatchHandler> matches = new HashMap<>();
    private static HashMap<Session, ClientThread> clients = new HashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) { //When client connects to the server
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress()); //Prints out the client ID
        sessions.add(session); //Adds session to a list
        System.out.println("Amount of connections: "+ sessions.size());
        // ---- Assigning a client -----
        ClientThread clientThread = new ClientThread(session); //Starting a thread for the client
        if(clientThreads.size() == 2){ // If there is two clients in the clientThreads list
            MatchHandler matchHandler = new MatchHandler(); //Start a match thread
            System.out.println("Clients is placed in match");
        }
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed with statusCode=" + statusCode + ", reason=" + reason);

    }

    @OnWebSocketMessage
    public void onMessage(String message, Session session) {

        String msg = String.format("Recived message: %s, From session: %s",message,session);

        System.out.println("Received message: " + message);
        Gson gson = new Gson();
        try {
            JsonObject jsonObject = gson.fromJson(message, JsonObject.class);
            String method = jsonObject.get("method").getAsString();

            if (method.equals("spawn")) {
                int type = jsonObject.get("type").getAsInt();

                matches.get(session).getGameManager().spawnCharacter(type, clients.get(session).getTeam());
            }

        } catch (Exception e) { }
    }


    private class ClientThread extends Thread{

        private Session session;

        int team;

        public ClientThread(Session session){
            this.session = session; //Assigns a specific session for this client
            clientThreads.add(this); //Adds this client to the client list
            System.out.println(clientThreads.toString());
            System.out.println("Client has been assign thread");
            start(); //starts the clients thread
        }
        public void sendJson(String json){
            try {
                session.getRemote().sendString(json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Session getSession() {
            return session;
        }

        public void setTeam(int team) {
            this.team = team;
        }

        public int getTeam() {
            return team;
        }

        public void run() {


        }
    }

    private class MatchHandler extends Thread{

        Object[] clientArray;
        ClientThread client1;
        ClientThread client2;

        GameManager gameManager;


        public MatchHandler(){
            clientArray = clientThreads.toArray(); //Creates a array from the set-list
            client1 = (ClientThread) clientArray[0]; //assigns the first client in the match
            client2 = (ClientThread) clientArray[1]; //assigns the second client in the match

            //This part removes the first two clients from the clientList, which lets the other clients in this list to take there place to then be assigned a match.
            clientThreads.remove(client1); //Removes the first client in the list
            clientThreads.remove(client2); //Removes the second client in the list

            matches.put(client1, this);
            matches.put(client2, this);

            clients.put(client1.getSession(), client1);
            clients.put(client2.getSession(), client2);

            client1.setTeam(0);
            client2.setTeam(1);

            start(); //Start the match thread
        }

        public GameManager getGameManager() {
            return gameManager;
        }

        public void spawnCharacter(String json) {
            client1.sendJson(json);
            client2.sendJson(json);
        }

        public void run(){


            /*
            ArrayList<String> list = new ArrayList<String>();

            gameManager = new GameManager();

            /*ArrayList<String> list = new ArrayList<String>();

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

             */
        }
    }
}